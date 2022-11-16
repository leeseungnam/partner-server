package kr.wrightbrothers.apps.order.dto;

import io.swagger.annotations.ApiModel;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.type.PaymentStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@ApiModel(value = "결제 정보")
@AllArgsConstructor
public class PaymentDto {
    private Long orderAmount;           // 주문 금액
    private Long deliveryChargeAmount;  // 배송료
    private Long paymentAmount;         // 결제 금액
    private String paymentDate;         // 결제 일자
    private String approvalNo;          // PG 승인번호
    private Long rentalAmount;          // 렌탈 금액
    private String paymentMethodCode;   // 결제 방법 코드
    private String paymentMethodName;   // 결제 방법 이름
    private String paymentStatusCode;   // 결제 상태 코드
    private String paymentStatusName;   // 결제 상태 이름

    public void setPaymentMethodName(String paymentMethodCode) {
        this.paymentMethodName = PaymentMethodCode.of(paymentMethodCode).getName();
    }

    public void setPaymentStatusName(String paymentStatusCode) {
        this.paymentStatusName = PaymentStatusCode.of(paymentStatusCode).getName();
    }
}
