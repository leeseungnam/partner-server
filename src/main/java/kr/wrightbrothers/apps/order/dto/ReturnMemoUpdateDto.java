package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnMemoUpdateDto {
    /** 주문 번호 */
    @NotBlank(message = "주문번호")
    private String orderNo;

    /** 반품 메모 */
    @Size(max = 2000, message = "반품메모")
    private String returnMemo;

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
