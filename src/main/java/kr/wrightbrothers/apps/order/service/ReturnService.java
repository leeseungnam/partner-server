package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import kr.wrightbrothers.apps.common.constants.ReasonConst;
import kr.wrightbrothers.apps.common.constants.DocumentSNS;
import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.queue.NotificationQueue;
import kr.wrightbrothers.apps.queue.OrderQueue;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReturnService {

    private final WBCommonDao dao;
    private final PaymentService paymentService;
    private final ResourceLoader resourceLoader;
    private final OrderQueue orderQueue;
    private final NotificationQueue notificationQueue;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Return.";
    private final String namespaceOrder = "kr.wrightbrothers.apps.order.query.Order.";

    public List<ReturnListDto.Response> findReturnList(ReturnListDto.Param paramDto,
                                                       boolean isRowBounds) {
        return isRowBounds ? dao.selectList(namespace + "findReturnList", paramDto, paramDto.getRowBounds(), PartnerKey.WBDataBase.Alias.Admin)
                : dao.selectList(namespace + "findReturnList", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public ReturnFindDto.Response findReturn(ReturnFindDto.Param paramDto) {
        return ReturnFindDto.Response.builder()
                .order(dao.selectOne(namespaceOrder + "findOrder", paramDto.toOrderFindParam(), PartnerKey.WBDataBase.Alias.Admin))
                .payment(paymentService.findPaymentToOrder(paramDto.toOrderFindParam()))
                .returnProductList(dao.selectList(namespace + "findReturnProductList", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                .build();
    }

    public void updateReturnMemo(ReturnMemoUpdateDto paramDto) {
        dao.update(namespace + "updateReturnMemo", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateRequestReturn(RequestReturnUpdateDto paramDto) {
        Arrays.stream(paramDto.getOrderProductSeqArray()).forEach(orderProductSeq -> {
            paramDto.setOrderProductSeq(orderProductSeq);
            String currentStatusCode = dao.selectOne(namespace + "findOrderProductStatusCode", paramDto, PartnerKey.WBDataBase.Alias.Admin);

            // 반품 요청에 대한 처리
            switch (OrderConst.ProductStatus.of(paramDto.getReturnProcessCode())) {
                case START_RETURN:
                case WITHDRAWAL_RETURN:
                    if (!OrderConst.ProductStatus.REQUEST_RETURN.getCode().equals(currentStatusCode))
                        break;

                    if (OrderConst.ProductStatus.START_RETURN.getCode().equals(paramDto.getReturnProcessCode())) {
                        // MultiQuery
                        // 반품 승인에 따른 진행 상태값 처리(택배정보는 필수값이 아니므로 존재 여부에 따른 수정 처리)
                        dao.update(namespace + "updateApprovalReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                        break;
                    }

                    dao.update(namespace + "updateWithdrawalReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                    break;
                case REQUEST_COMPLETE_RETURN:
                case NON_RETURN:
                    if (!OrderConst.ProductStatus.START_RETURN.getCode().equals(currentStatusCode))
                        break;

                    if (OrderConst.ProductStatus.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode())) {
                        // 반품배송비 처리는 판매금액이 가장 높은 주문상품에 몰빵 처리
                        dao.update(namespace + "updateRequestCompleteReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                        break;
                    }

                    // MultiQuery
                    // 주문상품 반품불가 상태변경 처리
                    dao.update(namespace + "updateNonReturn", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                    paramDto.setRequestValue(ReasonConst.NonReturn.of(paramDto.getRequestCode()).getName());
                    break;
            }
        });

        dao.update(namespaceOrder + "updateOrderStatusRefresh", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin);

        log.info("Order Return Process. OrderNo::{}, PartnerCode::{}, ReturnCode::{}", paramDto.getOrderNo(), paramDto.getPartnerCode(), paramDto.getReturnProcessCode());

        if (OrderConst.ProductStatus.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode()))
            dao.update(namespace + "updateReturnDeliveryAmount", paramDto, PartnerKey.WBDataBase.Alias.Admin);
        if (OrderConst.ProductStatus.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode()) &
                (boolean) dao.selectOne(namespace + "isPayMethodBank", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin))
            return;

        // 반품승인 알림톡 전송
        if (OrderConst.ProductStatus.START_RETURN.getCode().equals(paramDto.getReturnProcessCode())) {
            Arrays.stream(paramDto.getOrderProductSeqArray()).forEach(orderProductSeq -> {
                ReturnDeliveryDto.Response returnDelivery = findReturnDelivery(
                        ReturnDeliveryDto.Param.builder()
                                .partnerCode(paramDto.getPartnerCode())
                                .orderNo(paramDto.getOrderNo())
                                .orderProductSeq(orderProductSeq)
                                .build()
                );


                ReturnPartnerDto.Response partnerDto = ReturnPartnerDto.Response.builder().build();
                if (!"".equals(returnDelivery.getPartnerCode())) {
                    ReturnPartnerDto.ReqBody reqBody = ReturnPartnerDto.ReqBody.builder().prnrCd(returnDelivery.getPartnerCode()).build();
                    partnerDto = dao.selectOne(namespace + "findReturnPartner",
                            reqBody, PartnerKey.WBDataBase.Alias.Admin);
                }

                ReturnPartnerDto.Address address = dao.selectOne(namespace + "findReturnAddress",
                        ReturnPartnerDto.ReqBody.builder().prdtCd(returnDelivery.getProductCode()).build());

                if (!ObjectUtils.isEmpty(partnerDto) && !ObjectUtils.isEmpty(address)) {
                    notificationQueue.sendPushToAdmin(
                            DocumentSNS.NOTI_KAKAO_SINGLE
                            , Notification.CONFIRM_RETURN_ORDER
                            , returnDelivery.getRecipientPhone()
                            , new String[]{returnDelivery.getRecipientName(), paramDto.getOrderNo() + "-" + orderProductSeq, returnDelivery.getProductName(),
                                    address.getRtnAddr() + " " + address.getRtnAddrDtl(),
                                    partnerDto.getPrnrNm(), partnerDto.getCsPhn()});
                }
            });
        }

        // 주문 Queue 전송
        orderQueue.sendToAdmin(
                OrderConst.ProductStatus.REQUEST_COMPLETE_RETURN.getCode().equals(paramDto.getReturnProcessCode()) ?
                        DocumentSNS.REQUEST_RETURN_PRODUCT : DocumentSNS.UPDATE_ORDER_STATUS,
                OrderConst.ProductStatus.START_RETURN.getCode().equals(paramDto.getReturnProcessCode()) ?
                        paramDto.toApprovalQueueDto(paramDto.getReturnProcessCode()) : paramDto.toCancelQueueDto(paramDto.getReturnProcessCode()),
                PartnerKey.TransactionType.Update
        );
    }

    public ReturnDeliveryDto.Response findReturnDelivery(ReturnDeliveryDto.Param paramDto) {
        return dao.selectOne(namespace + "findReturnDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void updateReturnDelivery(ReturnDeliveryDto.ReqBody paramDto) {
        dao.update(namespace + "updateReturnDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 주문 Queue 전송
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
        ExcelUtil excel = new ExcelUtil(
                resourceLoader.getResource("classpath:templates/excel/returnList.xlsx").getInputStream(),
                1
        );

        if (ObjectUtils.isEmpty(paramDto.getReturnList())) {
            excel.excelWrite("반품목록리스트.xlsx", response);
            return;
        }

        List<ReturnExcelDto.Response> returnList = dao.selectList(namespace + "findExcelReturnList", paramDto, PartnerKey.WBDataBase.Alias.Admin);

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

        excel.excelWrite("반품목록리스트.xlsx", response);
    }
}