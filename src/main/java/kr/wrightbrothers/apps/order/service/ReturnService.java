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
        String orderNamespace = "kr.wrightbrothers.apps.order.query.Order.";

        return ReturnFindDto.Response.builder()
                // 주문내역 기본 정보
                .order(dao.selectOne(orderNamespace + "findOrder", OrderFindDto.Param.builder()
                                .partnerCode(paramDto.getPartnerCode())
                                .orderNo(paramDto.getOrderNo())
                        .build(), PartnerKey.WBDataBase.Alias.Admin))
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

        // 주문 상품 반품 상태값 변경 처리
        Arrays.stream(paramDto.getOrderProductSeqArray()).forEach(orderProductSeq -> {
            // 주문 상품 SEQ 설정
            paramDto.setOrderProductSeq(orderProductSeq);
            // 현재 주문 상품 상태 코드 조회
            String currentStatusCode = dao.selectOne(namespace + "findOrderProductStatusCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);


            // 반품 요청 처리
            switch (OrderProductStatusCode.of(paramDto.getReturnProcessCode())) {
                case START_RETURN:
                    // 반품 배송정보 확인
                    if (ObjectUtils.isEmpty(paramDto.getRequestCode()) || ObjectUtils.isEmpty(paramDto.getRequestName()) || ObjectUtils.isEmpty(paramDto.getRequestValue()))
                        throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"배송정보"});

                    // 배송정보 업데이트
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
                case COMPLETE_RETURN:
                case NON_RETURN:
                    // 반품 진행이 아닐 경우 예외
                    if (!OrderProductStatusCode.START_RETURN.getCode().equals(currentStatusCode))
                        break;

                    // 주문 상품 반품 완료 / 반품 불가 처리
                    dao.update(namespace + "updateOrderProductReturnCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);

                    // 주문 상태 변경 처리
                    dao.update(namespace + "updateOrderReturnCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);

                    // 반품 완료 요청 시 결제는 결제취소 요청으로 처리
                    if (OrderStatusCode.COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode())) {
                        dao.update("kr.wrightbrothers.apps.order.query.Payment.updateRequestCancelPayment",
                                PaymentCancelDto.builder()
                                        .orderNo(paramDto.getOrderNo())
                                        .userId(paramDto.getUserId())
                                        .partnerCode(paramDto.getPartnerCode())
                                        .build(), PartnerKey.WBDataBase.Alias.Admin);

                        // 반품 완료 요청에 대한 Queue 전송
                        orderQueue.sendToAdmin(
                                DocumentSNS.REQUEST_RETURN_PRODUCT,
                                // Queue 전송 데이터 객체 변환
                                paramDto.toCancelQueueDto(),
                                PartnerKey.TransactionType.Update
                                );
                    }
                    break;
            }
        });

        if (!OrderStatusCode.COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode()))
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
            int colIndex = 0;
            // 병합 사용 처리에 대한 카운트 처리
            ++excel.mergeCount;
            ++excel.subMergeCount;

            excel.row = excel.sheet.createRow(excel.rowNumber++);

            excel.setCellValue(colIndex++, returns.getRequestReturnDay());
            excel.setCellValue(colIndex++, returns.getOrderNo());
            excel.setCellValue(colIndex++, returns.getOrderDay());
            excel.setCellValue(colIndex++, returns.getOrderName());
            excel.setCellValue(colIndex++, returns.getProductCode());
            excel.setCellValue(colIndex++, returns.getProductName());
            excel.setCellValue(colIndex++, returns.getProductOption());
            excel.setCellValue(colIndex++, returns.getProductQty());
            excel.setCellValue(colIndex++, returns.getProductSellAmount());
            excel.setCellValue(colIndex++, returns.getProductAmount());
            excel.setCellValue(colIndex++, returns.getProductDeliveryChargeAmount());
            excel.setCellValue(colIndex++, returns.getPaymentAmount());
            excel.setCellValue(colIndex++, returns.getOrderUserName());
            excel.setCellValue(colIndex++, returns.getDeliveryType());
            excel.setCellValue(colIndex++, returns.getCompleteReturnDay());
            excel.setCellValue(colIndex++, returns.getReturnStatus());
            excel.setCellValue(colIndex++, returns.getDeliveryCompany());
            excel.setCellValue(colIndex++, returns.getInvoiceNo());
            excel.setCellValue(colIndex++, returns.getRecipientName());
            excel.setCellValue(colIndex++, returns.getRecipientUserPhone());
            excel.setCellValue(colIndex++, returns.getRecipientAddress());
            excel.setCellValue(colIndex++, returns.getRecipientAddressDetail());
            excel.setCellValue(colIndex, returns.getReason());

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