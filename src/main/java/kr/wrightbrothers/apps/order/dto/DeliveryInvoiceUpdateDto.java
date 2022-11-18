package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel(value = "송장번호 입력")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInvoiceUpdateDto {
    @ApiModelProperty(value = "주문 번호", required = true)
    @NotBlank(message = "주문 번호")
    private String orderNo;

    @ApiModelProperty(value = "주문 상품 SEQ", required = true)
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray; // 주문 상품 SEQ Array

    @ApiModelProperty(value = "택배사 코드", required = true)
    @NotBlank(message = "택배사 코드")
    private String deliveryCompanyCode;     // 택배사 코드

    @ApiModelProperty(value = "택배사 이름", required = true)
    @NotBlank(message = "택배사 이름")
    private String deliveryCompanyName;     // 택배사 이름

    @ApiModelProperty(value = "송장번호", required = true)
    @NotBlank(message = "송장번호")
    @Size(min = 2, max = 50, message = "송장번호")
    @Pattern(regexp = "^\\d+$", message = "송장번호는 숫자만 입력 가능 합니다.")
    private String invoiceNo;               // 택배 송장번호

    private String partnerCode;             // 파트너 코드
    @JsonIgnore
    private String userId;                  // 사용자 아이디
    @JsonIgnore
    private Integer orderProductSeq;        // 주문 상품 SEQ
    @JsonIgnore
    private String nonReturnFlag;           // 반품불가여부

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setAopUserId(String userId) {
        this.userId = userId;
    }

    public void setOrderProductSeq(Integer orderProductSeq) {
        this.orderProductSeq = orderProductSeq;
    }

    public void setNonReturnFlag(String nonReturnFlag) {
        this.nonReturnFlag = nonReturnFlag;
    }

    public Queue toQueueDto() {
        return Queue.builder()
                .ordNo(this.orderNo)
                .prnrCd(this.partnerCode)
                .stusCd(OrderStatusCode.START_DELIVERY.getCode())
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
