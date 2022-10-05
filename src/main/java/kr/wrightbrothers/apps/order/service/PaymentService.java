package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.PaymentCancelDto;
import kr.wrightbrothers.apps.order.dto.PaymentDto;
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
    private final String namespace = "kr.wrightbrothers.apps.order.query.Payment.";

    // 주문내역 상세정보 시 결제정보 조회
    public PaymentDto findPaymentToOrder(String orderNo) {
        return dao.selectOne(namespace + "findPaymentToOrder", orderNo);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateCancelPayment(PaymentCancelDto paramDto) {
        // 결제 취소 가능여부 체크
        // 주문완료 상태에서만 결제 취소 가능
        if (dao.selectOne(namespace + "isNonCompleteOrder", paramDto.getOrderNo())) {
            log.error("Payment Unable Cancel Payment. Reason::Not Complete Order Status, OrderNo::{}", paramDto.getOrderNo());
            throw new WBBusinessException(ErrorCode.UNABLE_CANCEL_PAYMENT.getErrCode(), new String[]{"주문 완료"});
        }

        // 결제 취소 요청
        updateForcedCancelPayment(paramDto);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateForcedCancelPayment(PaymentCancelDto paramDto) {
        // 중복 결제 취소 요청 여부 확인
        if (dao.selectOne(namespace + "isCancelPayment", paramDto.getOrderNo())) {
            log.error("Payment Already Cancel Payment. OrderNo::{}", paramDto.getOrderNo());
            throw new WBBusinessException(ErrorCode.ALREADY_CANCELED_PAYMENT.getErrCode());
        }

        // 부분취소 지원을 안하나 클라이언트에서 해당 주문 상품 목록의 체크박스를 통해 유입되는 관계로
        // 등록되어있는 주문상품 개수와, 클라이언트에서 전달된 취소 요청 개수와 일치 여부의 유효성 체크
        if (dao.selectOne(namespace + "isCancelPartialPayment", paramDto)) {
            log.error("Unable Cancel Partial Payment. OrderNo::{}", paramDto.getOrderNo());
            throw new WBBusinessException(ErrorCode.UNABLE_CANCEL_PARTIAL_PAYMENT.getErrCode());
        }

        // 결제 취소 / 주문 취소  요청 & 사유 등록
        // 주문, 결제 상태 변경 요청 코드
        // S09 결제 취소 요청, O06 주문 취소 요청
        dao.update(namespace + "updateRequestCancelPayment", paramDto);
        dao.update(namespace + "updateRequestCancelOrder", paramDto);
        // 무통장 결제 시 환불 계좌 등록
        if (PaymentMethodCode.NON_BANK.getCode().equals(paramDto.getPaymentMethodCode())) {
            // 필수 입력값 체크
            paramDto.validRefundInfo();
            // 환불정보 입력
            dao.update(namespace + "updatePaymentDetailRefundInfo", paramDto);
        }

        log.debug("Update Request Cancel Payment. OrderNo::{}", paramDto.getOrderNo());
    }
}
