package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalReturnDto {
    /** 주문번호 */
    @NotBlank(message = "주문 번호")
    private String orderNo;

    /** 주문 상품 SEQ */
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray;

    /** 택배 회사 코드 */
    private String deliveryCompanyCode;

    /** 운송장 번호 */
    private String invoiceNo;

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

    public RequestReturnUpdateDto toRequestReturnUpdateDto() {
        return RequestReturnUpdateDto.builder()
                .orderNo(this.orderNo)
                .orderProductSeqArray(this.orderProductSeqArray)
                .returnProcessCode(OrderConst.ProductStatus.START_RETURN.getCode())
                .requestCode(this.deliveryCompanyCode)
                .requestValue(this.invoiceNo)
                .partnerCode(this.partnerCode)
                .userId(this.userId)
                .build();
    }
}
