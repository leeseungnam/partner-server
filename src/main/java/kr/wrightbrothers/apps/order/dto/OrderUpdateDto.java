package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
public class OrderUpdateDto {
    /** 주문 번호 */
    @NotBlank(message = "주문 번호")
    private String orderNo;

    /** 수령자 명 */
    @NotBlank(message = "수령자명")
    @Size(min = 2, max = 20, message = "수령자 명")
    private String recipientName;

    /** 휴대전화 */
    @NotBlank(message = "휴대전화")
    @Size(min = 10, max = 11, message = "휴대전화")
    @Pattern(regexp = "^\\d+$", message = "휴대전화는 숫자만 입력 가능 합니다.")
    private String recipientPhone;

    /** 우편번호 */
    @NotBlank(message = "우편번호")
    @Size(min = 5, max = 5, message = "우편번호")
    private String recipientAddressZipCode;

    /** 주소 */
    @NotBlank(message = "주소")
    private String recipientAddress;

    /** 상세주소 */
    @NotBlank(message = "상세주소")
    @Size(min = 2, max = 100, message = "상세주소")
    private String recipientAddressDetail;

    /** 파트너 코드 */
    private String partnerCode;

    /** 사용자 아이디 */
    @JsonIgnore
    private String userId;

    /** 택배 송장번호 등록 여부 */
    @JsonIgnore
    private boolean isInvoiceNo;

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
    public void setAopUserId(String userId) {
        this.userId = userId;
    }

    @Getter @Builder
    public static class Status {
        /** 주문 번호 */
        private String orderNo;

        /** 주문 진행 코드 */
        private String orderStatusCode;

        /** 사용자 아이디 */
        private String userId;
    }

}
