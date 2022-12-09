package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.queue.OrderQueue;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(value = "orderPaymentService")
@RequiredArgsConstructor
public class PaymentService {

    private final WBCommonDao dao;
    private final OrderQueue orderQueue;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Payment.";

    public PaymentDto findPaymentToOrder(OrderFindDto.Param paramDto) {
        return dao.selectOne(namespace + "findPaymentToOrder", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public PaymentRefundDto.ResBody findPaymentRefundAccount(PaymentRefundDto.Param paramDto) {
        return dao.selectOne(namespace + "findPaymentRefundAccount", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void updatePaymentRefundAccount(PaymentRefundDto.ReqBody paramDto) {
        dao.update(namespace + "updatePaymentRefundAccount", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateCancelPayment(PaymentCancelDto paramDto) {
        if (dao.selectOne(namespace + "isCancelPartialPayment", paramDto, PartnerKey.WBDataBase.Alias.Admin)) {
            throw new WBBusinessException(ErrorCode.UNABLE_CANCEL_PARTIAL_PAYMENT.getErrCode());
        }
        if (dao.selectOne(namespace + "isAfterOrderComplete", paramDto, PartnerKey.WBDataBase.Alias.Admin)) {
            throw new WBBusinessException(ErrorCode.UNABLE_CANCEL_PAYMENT.getErrCode(), new String[]{"주문완료"});
        }

        // MultiQuery
        // 주문 취소에 대한 상대값 변경 처리
        dao.update(namespace + "updateRequestCancelPayment", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        if (PaymentMethodCode.NON_BANK.getCode().equals(paramDto.getPaymentMethodCode())) {
            paramDto.validRefundInfo();
            dao.update(namespace + "updatePaymentDetailRefundInfo", paramDto, PartnerKey.WBDataBase.Alias.Admin);
        }

        PaymentDto payment =
                dao.selectOne(namespace + "findPaymentToOrder",
                        new OrderFindDto.Param(paramDto.getPartnerCode(), paramDto.getOrderNo()),
                        PartnerKey.WBDataBase.Alias.Admin
                );

        log.info("Order Payment Request Cancel. OrderNo::{}, PartnerCode::{}", paramDto.getOrderNo(), paramDto.getPartnerCode());

        // 결제취소 요청에 대한 Queue 전송
        orderQueue.sendToAdmin(
                DocumentSNS.REQUEST_CANCEL_PAYMENT,
                paramDto.toCancelQueueDto(
                        PaymentCancelDto.BankInfo.builder()
                                .bankCd(paramDto.getRefundBankCode())
                                .bankAcntNo(paramDto.getRefundBankAccountNo())
                                .dpstrNm(paramDto.getRefundDepositorName())
                                .build(),
                        payment.getPaymentAmount()),
                PartnerKey.TransactionType.Update
                );
    }

}
