package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
public class OrderMemoUpdateDto {
    /** 주문 번호 */
    @NotBlank(message = "주문 번호")
    private String orderNo;

    /** 주문 메모 */
    @Size(max = 2000, message = "주문메모")
    private String orderMemo;

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
}
