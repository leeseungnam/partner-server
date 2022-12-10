package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPreparingDto {
    /** 주문번호 */
    @NotBlank(message = "주문번호")
    private String orderNo;

    /** 파트너 코드 */
    private String partnerCode;

    /** 사용자 아이디 */
    @JsonIgnore
    private String userId;

    /** 데이터1 */
    @JsonIgnore
    private String value1;

    /** 데이터2 */
    @JsonIgnore
    private String value2;

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
    public void setAopUserId(String userId) {
        this.userId = userId;
    }

    public Queue toQueueDto(List<Integer> ordPrdtIdx) {
        return Queue.builder()
                .ordNo(this.orderNo)
                .prnrCd(this.partnerCode)
                .stusCd(OrderConst.Status.READY_PRODUCT.getCode())
                .ordPrdtIdx(ordPrdtIdx.stream().map(String::valueOf).collect(Collectors.toList()))
                .rsnCd(this.value1)
                .rsnNm(this.value2)
                .usrId(this.userId)
                .build();
    }

    public Queue toApprovalQueueDto(List<Integer> ordPrdtIdx) {
        return Queue.builder()
                .ordNo(this.orderNo)
                .prnrCd(this.partnerCode)
                .stusCd(OrderConst.Status.READY_PRODUCT.getCode())
                .ordPrdtIdx(ordPrdtIdx.stream().map(String::valueOf).collect(Collectors.toList()))
                .dlvrCmpnyCd(this.value1)
                .invcNo(this.value2)
                .usrId(this.userId)
                .build();
    }

    @Getter
    @Builder
    public static class Queue {
        /** 주문 번호 */
        private String ordNo;

        /** 파트너 코드 */
        private String prnrCd;

        /** 상태 코드 */
        private String stusCd;

        /** 주문상품 IDX LIST */
        private List<String> ordPrdtIdx;

        /** 반품불가 사유 코드 */
        private String rsnCd;

        /** 반품불가 사유 명 */
        private String rsnNm;

        /** 택배사 코드 */
        private String dlvrCmpnyCd;

        /** 송장번호 */
        private String invcNo;

        /** 사용자 아이디 */
        private String usrId;
    }

}
