package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.type.PaymentStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentDto {
    /** 주문 금액 */
    private Long orderAmount;

    /** 배송료 */
    private Long deliveryChargeAmount;

    /** 결제 금액 */
    private Long paymentAmount;

    /** 결제 일자 */
    private String paymentDate;

    /** PG 승인번호 */
    private String approvalNo;

    /** 렌탈 금액 */
    private Long rentalAmount;

    /** 결제 방법 코드 */
    private String paymentMethodCode;

    /** 결제 방법 이름 */
    private String paymentMethodName;

    /** 결제 상태 코드 */
    private String paymentStatusCode;

    /** 결제 상태 이름 */
    private String paymentStatusName;

    public void setPaymentMethodName(String paymentMethodCode) {
        this.paymentMethodName = PaymentMethodCode.of(paymentMethodCode).getName();
    }

    public void setPaymentStatusName(String paymentStatusCode) {
        this.paymentStatusName = PaymentStatusCode.of(paymentStatusCode).getName();
    }
}
