package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPickupUpdateDto {
    /** 주문번호 */
    @NotBlank(message = "주문 번호")
    private String orderNo;

    /** 주문상품 SEQ Array */
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray;

    /** 파트너 코드 */
    private String partnerCode;

    /** 사용자 아이디 */
    @JsonIgnore
    private String userId;

    /** 반품불가 여부 */
    @JsonIgnore
    private String nonReturnFlag;

    /** 배송완료 코드 */
    @JsonIgnore
    private String completeCode;

    public void setCompleteCode(String completeCode) {
        this.completeCode = completeCode;
    }

    public void setNonReturnFlag(String nonReturnFlag) {
        this.nonReturnFlag = nonReturnFlag;
    }

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setAopUserId(String userId) {
        this.userId = userId;
    }

    public DeliveryInvoiceUpdateDto toDeliveryInvoiceUpdateDto() {
        return DeliveryInvoiceUpdateDto.builder()
                .orderNo(this.orderNo)
                .orderProductSeqArray(this.orderProductSeqArray)
                .build();
    }

    public DeliveryFreightUpdateDto.Queue toQueueDto() {
        return DeliveryFreightUpdateDto.Queue.builder()
            .ordNo(this.orderNo)
            .prnrCd(this.partnerCode)
            .stusCd(OrderConst.Status.FINISH_DELIVERY.getCode())
            .ordPrdtIdx(Arrays.stream(this.orderProductSeqArray).map(String::valueOf).collect(
                Collectors.toList()))
            .usrId(this.userId)
            .build();
    }

    @Getter
    @Builder
    public static class Queue {
        /** 주문번호 */
        private String ordNo;

        /** 파트너 코드 */
        private String prnrCd;

        /** 상태 코드 */
        private String stusCd;

        /** 주문상품 IDX LIST */
        private List<String> ordPrdtIdx;

        /** 사용자 아이디 */
        private String usrId;
    }
}
