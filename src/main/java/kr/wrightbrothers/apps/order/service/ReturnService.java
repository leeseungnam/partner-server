package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.type.NonReturnCode;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.queue.OrderQueue;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final WBCommonDao dao;
    private final PaymentService paymentService;
    private final ResourceLoader resourceLoader;
    private final OrderQueue orderQueue;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Return.";
    private final String namespaceOrder = "kr.wrightbrothers.apps.order.query.Order.";

    public List<ReturnListDto.Response> findReturnList(ReturnListDto.Param paramDto) {
        // 반품관리 목록 조회
        return dao.selectList(namespace + "findReturnList", paramDto, paramDto.getRowBounds(), PartnerKey.WBDataBase.Alias.Admin);
    }

    public ReturnFindDto.Response findReturn(ReturnFindDto.Param paramDto) {
        return ReturnFindDto.Response.builder()
                // 주문 기본 정보
                .order(dao.selectOne(namespaceOrder + "findOrder", OrderFindDto.Param.builder()
                        .partnerCode(paramDto.getPartnerCode())
                        .orderNo(paramDto.getOrderNo())
                        .build(), PartnerKey.WBDataBase.Alias.Admin))
                // 결제 정보
                .payment(paymentService.findPaymentToOrder(OrderFindDto.Param.builder()
                        .partnerCode(paramDto.getPartnerCode())
                        .orderNo(paramDto.getOrderNo())
                        .build()))
                // 반품 상품 리스트
                .returnProductList(dao.selectList(namespace + "findReturnProductList", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                .build();
    }

    public void updateReturnMemo(ReturnMemoUpdateDto paramDto) {
        dao.update(namespace + "updateReturnMemo", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateRequestReturn(RequestReturnUpdateDto paramDto) {
        Arrays.stream(paramDto.getOrderProductSeqArray()).forEach(orderProductSeq -> {
            // 주문 상품 SEQ 설정
            paramDto.setOrderProductSeq(orderProductSeq);
            // 현재 주문 상품 상태 코드 조회
            String currentStatusCode = dao.selectOne(namespace + "findOrderProductStatusCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);

            // 반품 요청 처리
            switch (OrderProductStatusCode.of(paramDto.getReturnProcessCode())) {
                case START_RETURN:
                case WITHDRAWAL_RETURN:
                    // 반품승인, 반품불가 처리는 반품요청 상태에서만 허용됨
                    if (!OrderProductStatusCode.REQUEST_RETURN.getCode().equals(currentStatusCode))
                        break;

                    // 반품승인 상태 처리(MultiQuery)
                    // 주문 배송정보의 택배사, 송장번호.
                    // 주문 상품의 상태 반품진행 상태변경
                    if (OrderProductStatusCode.START_RETURN.getCode().equals(paramDto.getReturnProcessCode())) {
                        dao.update(namespace + "updateApprovalReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                        break;
                    }

                    // 반품취소 주문상품 상태변경
                    dao.update(namespace + "updateWithdrawalReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                    break;
                case REQUEST_COMPLETE_RETURN:
                case NON_RETURN:
                    // 반품완료, 반품불가 처리는 반품진행 상태에서만 허용됨
                    if (!OrderProductStatusCode.START_RETURN.getCode().equals(currentStatusCode))
                        break;

                    // 반품 완료 요청 시 결제는 결제취소 요청으로 처리
                    if (OrderProductStatusCode.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode())) {
                        dao.update(namespace + "updateRequestCompleteReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                        Reason reason = dao.selectOne(namespace + "findReturnReason", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                        paramDto.setRequestCode(reason.getReasonCode());
                        paramDto.setRequestValue(reason.getReasonValue());
                        break;
                    }

                    // 반품불가 요청에 따른 처리(Multi Query)
                    dao.update(namespace + "updateNonReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                    paramDto.setRequestValue(NonReturnCode.of(paramDto.getRequestCode()).getName());
                    break;
            }
        });
        // 대표 주문, 결제 상태코드 갱신 공통 프로시져 호출
        dao.update(namespaceOrder + "updateOrderStatusRefresh", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin);

        // 반품완료 환불금액 처리
        if (OrderProductStatusCode.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode()))
            dao.update(namespace + "updateReturnDeliveryAmount", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 무통장 반품완료 요청은 SNS 전송 제외
        if (OrderProductStatusCode.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode()) &
                (boolean) dao.selectOne(namespace + "isPayMethodBank", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin))
            return;

        orderQueue.sendToAdmin(
                OrderProductStatusCode.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode()) ?
                        DocumentSNS.REQUEST_RETURN_PRODUCT : DocumentSNS.UPDATE_ORDER_STATUS,
                // Queue 전송 데이터 객체 변환
                OrderProductStatusCode.START_RETURN.getCode().equals(paramDto.getReturnProcessCode()) ?
                        paramDto.toApprovalQueueDto(paramDto.getReturnProcessCode()) : paramDto.toCancelQueueDto(paramDto.getReturnProcessCode()),
                PartnerKey.TransactionType.Update
        );
    }

    public ReturnDeliveryDto.Response findReturnDelivery(ReturnDeliveryDto.Param paramDto) {
        // 반품지 배송정보 조회
        return dao.selectOne(namespace + "findReturnDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void updateReturnDelivery(ReturnDeliveryDto.ReqBody paramDto) {
        // 반품지 배송정보 수정
        dao.update(namespace + "updateReturnDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 수정 Queue 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER,
                DeliveryPreparingDto.Queue.builder()
                        .ordNo(paramDto.getOrderNo())
                        .prnrCd(paramDto.getPartnerCode())
                        .stusCd(dao.selectOne(namespace + "findOrderProductStatus", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                        .ordPrdtIdx(List.of(String.valueOf(paramDto.getOrderProductSeq())))
                        .dlvrCmpnyCd(paramDto.getDeliveryCompanyCode())
                        .invcNo(paramDto.getInvoiceNo())
                        .usrId(paramDto.getUserId())
                        .build(),
                PartnerKey.TransactionType.Update
        );
    }

    public void makeExcelFile(ReturnExcelDto.Param paramDto,
                              HttpServletResponse response) throws IOException {
        // 엑셀 템플릿 초기화
        ExcelUtil excel = new ExcelUtil(
                resourceLoader.getResource("classpath:templates/excel/returnList.xlsx").getInputStream(),
                1
        );

        List<ReturnExcelDto.Response> returnList = dao.selectList(namespace + "findExcelReturnList", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 엑셀 시트 생성
        excel.sheet = excel.workbook.getSheetAt(0);

        // 엑셀 생성
        returnList.forEach(returns -> {
            int colIndex = 23;
            // 병합 사용 처리에 대한 카운트 처리
            ++excel.mergeCount;
            ++excel.subMergeCount;

            excel.row = excel.sheet.createRow(excel.rowNumber++);
            excel.setCellValue(returns);

            // 주문번호 기준 셀 병합처리
            if (excel.mergeCount == returns.getOrderProductCount()) {
                if (excel.mergeCount > 1)
                    for (int col = 0; col <= colIndex; col++) {
                        if ((col > 0 & col < 4) | (col > 10 & col < 13) | (col > 17 & col < 22))
                            excel.sheet.addMergedRegion(new CellRangeAddress(excel.rowNumber - excel.mergeCount, excel.rowNumber - 1, col, col));
                    }

                excel.mergeCount = 0;
            }

            // 상품코드 기준 셀 병합처리
            if (excel.subMergeCount == returns.getProductCount()) {
                if (excel.subMergeCount > 1)
                    for (int col = 0; col <= colIndex; col++) {
                        if (col > 3 & col <6)
                            excel.sheet.addMergedRegion(new CellRangeAddress(excel.rowNumber - excel.subMergeCount, excel.rowNumber - 1, col, col));
                    }

                excel.subMergeCount = 0;
            }
        });

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode("반품목록리스트.xlsx", StandardCharsets.UTF_8) + "\";");
        excel.workbook.write(response.getOutputStream());
        excel.workbook.close();
    }

    @Getter
    @AllArgsConstructor
    static
    class Reason {
        private String reasonCode;
        private String reasonValue;
    }
}