package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
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

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryFreightUpdateDto {

    @ApiModelProperty(value = "주문 번호", required = true)
    @NotBlank(message = "주문 번호")
    private String orderNo;                     // 주문번호

    @ApiModelProperty(value = "주문 상품 SEQ", required = true)
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray;     // 주문상품 SEQ Array

    private String partnerCode;                 // 파트너코드
    @JsonIgnore
    private String userId;                      // 사용자 아이디

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

    @Getter
    @Builder
    public static class Queue {
        private String ordNo;               // 주문번호
        private String prnrCd;              // 파트너코드
        private String stusCd;              // 상태코드
        private List<String> ordPrdtIdx; // 주문상품 IDX 배열
        private String usrId;               // 로그인 아이디
    }
}
