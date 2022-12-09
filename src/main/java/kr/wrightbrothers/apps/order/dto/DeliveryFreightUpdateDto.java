package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryFreightUpdateDto {

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

    public void setNonReturnFlag(String nonReturnFlag) {
        this.nonReturnFlag = nonReturnFlag;
    }

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setAopUserId(String userId) {
        this.userId = userId;
    }

    public Queue toQueueDto() {
        return Queue.builder()
                .ordNo(this.orderNo)
                .prnrCd(this.partnerCode)
                .stusCd(OrderStatusCode.FINISH_DELIVERY.getCode())
                .ordPrdtIdx(Arrays.stream(this.orderProductSeqArray).map(String::valueOf).collect(Collectors.toList()))
                .usrId(this.userId)
                .build();
    }

    @Getter @Builder
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
