package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import kr.wrightbrothers.apps.common.constants.ReasonConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonReturnDto {
    /** 주문 번호 */
    @NotBlank(message = "주문 번호")
    private String orderNo;

    /** 주문 상품 SEQ Array */
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray;

    /** 블기 시우 */
    @NotNull(message = "불가 사유")
    private String reasonCode;

    /** 파트너 코드 */
    private String partnerCode;

    /** 사용자 아이디 */
    @JsonIgnore
    private String userId;

    public RequestReturnUpdateDto toRequestReturnUpdateDto() {
        return RequestReturnUpdateDto.builder()
                .orderNo(this.orderNo)
                .orderProductSeqArray(this.orderProductSeqArray)
                .partnerCode(this.partnerCode)
                .returnProcessCode(OrderConst.ProductStatus.NON_RETURN.getCode())
                .requestCode(this.reasonCode)
                .requestValue(ReasonConst.NonReturn.of(this.reasonCode).getName())
                .userId(this.userId)
                .build();
    }

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
    public void setAopUserId(String userId) {
        this.userId = userId;
    }
}
