package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPreparingDto {
    @NotBlank(message = "주문번호")
    private String orderNo;

    private String partnerCode;             // 파트너 코드
    @JsonIgnore
    private String userId;                  // 사용자 아이디
    @JsonIgnore
    private String value1;                  // 데이터1
    @JsonIgnore
    private String value2;                  // 데이터2

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
                .stusCd(OrderStatusCode.READY_PRODUCT.getCode())
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
                .stusCd(OrderStatusCode.READY_PRODUCT.getCode())
                .ordPrdtIdx(ordPrdtIdx.stream().map(String::valueOf).collect(Collectors.toList()))
                .dlvrCmpnyCd(this.value1)
                .invcNo(this.value2)
                .usrId(this.userId)
                .build();
    }

    @Getter
    @Builder
    public static class Queue {
        private String ordNo;               // 주문번호
        private String prnrCd;              // 파트너코드
        private String stusCd;              // 상태코드
        private List<String> ordPrdtIdx;    // 주문상품 IDX 배열
        private String rsnCd;               // 반품불가 코드
        private String rsnNm;               // 반품불가 이름
        private String dlvrCmpnyCd;         // 배송사 코드
        private String invcNo;              // 송장번호
        private String usrId;               // 로그인 아이디
    }

}
