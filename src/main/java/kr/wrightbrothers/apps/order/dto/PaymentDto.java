package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.type.PaymentStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentDto {
    private Long orderAmount;           // 주문 금액
    private Long deliveryChargeAmount;  // 배송료
    private Long sspPoint;              // SSP 포인트
    private Long salesAmount;           // 판매 대금
    private Long paymentAmount;         // 결제 금액
    private String paymentDate;         // 결제 일자
    private String transactionId;       // PG 승인번호
    private String paymentMethodName;   // 결제 방법
    private String paymentStatusName;   // 결제 상태
    private String cancelDate;          // 취소 일시
    private String cancelReason;        // 취소 사유

    public void setPaymentMethodName(String paymentMethodCode) {
        this.paymentMethodName = PaymentMethodCode.of(paymentMethodCode).getName();
    }

    public void setPaymentStatusName(String paymentStatusCode) {
        this.paymentStatusName = PaymentStatusCode.of(paymentStatusCode).getName();
    }
}
