package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.queue.OrderQueue;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final WBCommonDao dao;
    private final PaymentService paymentService;
    private final ResourceLoader resourceLoader;
    private final OrderQueue orderQueue;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Return.";

    public List<ReturnListDto.Response> findReturnList(ReturnListDto.Param paramDto) {
        // 반품관리 목록 조회
        return dao.selectList(namespace + "findReturnList", paramDto, paramDto.getRowBounds(), PartnerKey.WBDataBase.Alias.Admin);
    }

    public ReturnFindDto.Response findReturn(ReturnFindDto.Param paramDto) {
        return ReturnFindDto.Response.builder()
                // 주문 기본 정보(반품 배송지 정보의 필드명은 수령 배송지 정보의 필드와 동일하게 처리, DTO 재활용을 위해)
                .order(dao.selectOne(namespace + "findReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                // 결제 정보
                .payment(paymentService.findPaymentToOrder(OrderFindDto.Param.builder()
                                .partnerCode(paramDto.getPartnerCode())
                                .orderNo(paramDto.getOrderNo())
                        .build()))
                // 주문 상품 리스트(취소 상품 제외)
                .orderProductList(dao.selectList(namespace + "findNonCancelOrderProduct", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                // 반품 요청 상품 리스트
                .returnProductList(dao.selectList(namespace + "findReturnProductList", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                .build();
    }

    /**
     * <pre>
     * 반품 관련해서 해당 부분의 송장번호가 입력 되어있으면, 배송지 정보 수정을 하지 않음.
     * 반품 배송지의 필드명은 수령 배송지의 필드명과 동일하게 하였으며, 해당부분은 DTO 재활용을 위한 처리.
     * 해당부분 인지하고 개발 필요 함.
     *
     * 반품에 관련된 사항이라 예외를 하여도 되지만,
     * 위 사항은 혹시 모를 사항을 생각하여 해당 부분 로직을 추가 처리.
     * </pre>
     */
    public void updateReturn(ReturnMemoUpdateDto paramDto) {
        // 송장번호 입력 시 배송지 정보 수정 제외
        dao.update(namespace + "updateReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateRequestReturn(RequestReturnUpdateDto paramDto) {
        // 반품불가 처리 시 유효성 확인
        if (OrderProductStatusCode.NON_RETURN.getCode().equals(paramDto.getReturnProcessCode())) {
            if (ObjectUtils.isEmpty(paramDto.getRequestCode()) || ObjectUtils.isEmpty(paramDto.getRequestName()))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"불가 사유"});
        }

        // 중복 반품 요청 확인
        if (dao.selectOne(namespace + "isRequestReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.ALREADY_RETURN.getErrCode(), new String[]{OrderStatusCode.of(paramDto.getReturnProcessCode()).getName()});

        AtomicInteger requestCompleteReturnCount = new AtomicInteger();
        // 주문 상품 반품 상태값 변경 처리
        Arrays.stream(paramDto.getOrderProductSeqArray()).forEach(orderProductSeq -> {
            // 주문 상품 SEQ 설정
            paramDto.setOrderProductSeq(orderProductSeq);
            // 현재 주문 상품 상태 코드 조회
            String currentStatusCode = dao.selectOne(namespace + "findOrderProductStatusCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);

            // 반품 요청 처리
            switch (OrderProductStatusCode.of(paramDto.getReturnProcessCode())) {
                case START_RETURN:
                    // 반품 배송정보 수정
                    if (!ObjectUtils.isEmpty(paramDto.getRequestCode()) & !ObjectUtils.isEmpty(paramDto.getRequestValue()))
                        dao.update(namespace + "updateOrderDeliveryReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);

                    // 아래 반품 취소 조건으로 진행을 이어나가게 하며,
                    // 단순 배송 정보 수정에 대해서는 아래 반품 요청이 아니므로 진행 중지가 됨.
                case WITHDRAWAL_RETURN:
                    // 반품 요청이 아닐 경우 예외
                    if (!OrderProductStatusCode.REQUEST_RETURN.getCode().equals(currentStatusCode))
                        break;

                    // 주문 상품 반품 진행 / 반품 철회 처리
                    dao.update(namespace + "updateOrderProductReturnCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);

                    // 반품 취소 시 주문 상태 값은 반품요청 -> 배송완료 변경되야 함.
                    if (OrderProductStatusCode.WITHDRAWAL_RETURN.getCode().equals(paramDto.getRequestCode()))
                        paramDto.setReturnProcessCode(OrderStatusCode.FINISH_DELIVERY.getCode());

                    // 주문 상태 변경 처리
                    dao.update(namespace + "updateOrderReturnCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                    break;
                case REQUEST_COMPLETE_RETURN:
                case NON_RETURN:
                    // 반품 진행이 아닐 경우 예외
                    if (!OrderProductStatusCode.START_RETURN.getCode().equals(currentStatusCode))
                        break;

                    // 주문 상품 반품 완료 요청 / 반품 불가 처리
                    dao.update(namespace + "updateOrderProductReturnCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);

                    // 주문 상태 변경 처리
                    dao.update(namespace + "updateOrderReturnCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);

                    // 반품 완료 요청 시 결제는 결제취소 요청으로 처리
                    if (OrderStatusCode.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode())) {
                        dao.update(namespace + "updateRequestReturnOrderPartner", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                        requestCompleteReturnCount.incrementAndGet();
                    }

                    break;
            }
        });

        // 반품 완료 요청에 대한 Queue 전송
        if (requestCompleteReturnCount.get() > 0) {
            PaymentCancelDto.BankInfo bankInfo =
                    dao.selectOne("kr.wrightbrothers.apps.order.query.Payment.findBankInfo", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin);

            orderQueue.sendToAdmin(
                    DocumentSNS.REQUEST_RETURN_PRODUCT,
                    // Queue 전송 데이터 객체 변환
                    paramDto.toCancelQueueDto(bankInfo),
                    PartnerKey.TransactionType.Update
            );
        }

        if (!OrderStatusCode.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode()))
            // 반품완료 요청 제외한 나머지 프로시저 호출로 주문정보 상태값 변경
            dao.update("kr.wrightbrothers.apps.order.query.Order.updateOrderStatusRefresh", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin);
    }

    public void makeExcelFile(ReturnExcelDto.Param paramDto,
                              HttpServletResponse response) throws IOException {
        // 엑셀 템플릿 초기화
        ExcelUtil excel = new ExcelUtil(
                new FileInputStream(resourceLoader.getResource("classpath:templates/excel/returnList.xlsx").getFile()),
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
}