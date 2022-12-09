package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestReturnDto {
    /** 주문 번호 */
    @NotBlank(message = "주문 번호")
    private String orderNo;

    /** 주문 상품 SEQ Array */
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray;

    /** 반품 배송비 */
    @NotNull(message = "반품 배송비")
    private Long returnDeliveryAmount;

    /** 결제 금액 */
    @NotNull(message = "결제 금액")
    private Long paymentAmount;

    /** 환불 예정 금액 */
    @NotNull(message = "환불 예정 금액")
    private Long refundAmount;

    /** 파트너 코드 */
    private String partnerCode;

    /** 사용자 아이디 */
    @JsonIgnore
    private String userId;

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setAopUserId(String userId) {
        this.userId = userId;
    }

    // 유효성 검사
    public void valid() {
        if (this.refundAmount < 0)
            throw new WBBusinessException(ErrorCode.INVALID_NUMBER_MIN.getErrCode(), new String[]{"환불 예정 금액", "0"});
        if (this.refundAmount > this.paymentAmount)
            throw new WBBusinessException(ErrorCode.INVALID_MONEY_MAX.getErrCode(), new String[]{"환불 예정 금액", String.valueOf(this.paymentAmount)});
    }

    public RequestReturnUpdateDto toRequestReturnUpdateDto(String processCode) {
        return RequestReturnUpdateDto.builder()
                .orderNo(this.orderNo)
                .orderProductSeqArray(this.orderProductSeqArray)
                .partnerCode(this.partnerCode)
                .returnProcessCode(processCode)
                .returnDeliveryAmount(this.returnDeliveryAmount)
                .paymentAmount(this.paymentAmount)
                .refundAmount(this.refundAmount)
                .userId(this.userId)
                .build();
    }
}
