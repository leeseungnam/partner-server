package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

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
                .ordPrdtIdx(ordPrdtIdx)
                .usrId(this.userId)
                .build();
    }


    @Getter
    @Builder
    public static class Queue {
        private String ordNo;               // 주문번호
        private String prnrCd;              // 파트너코드
        private List<Integer> ordPrdtIdx; // 주문상품 IDX 배열
        private String usrId;               // 로그인 아이디
    }

}
