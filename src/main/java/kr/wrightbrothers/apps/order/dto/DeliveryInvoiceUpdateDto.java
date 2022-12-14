package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.constants.OrderConst;
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

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInvoiceUpdateDto {
    /** 주문 번호 */
    @NotBlank(message = "주문 번호")
    private String orderNo;

    /** 주문상품 SEQ Array */
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray;

    /** 택배사 코드 */
    @NotBlank(message = "택배사 코드")
    private String deliveryCompanyCode;

    /** 택배사 이름 */
    @NotBlank(message = "택배사 이름")
    private String deliveryCompanyName;

    /** 송장번호 */
    @NotBlank(message = "송장번호")
    @Size(min = 2, max = 50, message = "송장번호")
    @Pattern(regexp = "^\\d+$", message = "송장번호는 숫자만 입력 가능 합니다.")
    private String invoiceNo;

    /** 파트너 코드 */
    private String partnerCode;

    /** 사용자 아이디 */
    @JsonIgnore
    private String userId;

    /** 주문 상품 SEQ */
    @JsonIgnore
    private Integer orderProductSeq;

    /** 반품불가 여부 */
    @JsonIgnore
    private String nonReturnFlag;

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

    public DeliveryUpdateDto toDeliveryUpdateDto() {
        return DeliveryUpdateDto.builder()
                .orderNo(this.orderNo)
                .orderProductSeqArray(this.orderProductSeqArray)
                .build();
    }

    public Queue toQueueDto() {
        return Queue.builder()
                .ordNo(this.orderNo)
                .prnrCd(this.partnerCode)
                .stusCd(OrderConst.Status.START_DELIVERY.getCode())
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
