package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
public class OrderUpdateDto {
    @NotBlank(message = "주문 번호")
    private String orderNo;                 // 주문 번호

    @NotBlank(message = "수령자명")
    @Size(min = 2, max = 20, message = "수령자명")
    private String recipientName;           // 수령자 이름

    @NotBlank(message = "휴대전화")
    @Size(min = 10, max = 11, message = "휴대전화")
    @Pattern(regexp = "^\\d+$", message = "휴대전화는 숫자만 입력 가능 합니다.")
    private String recipientPhone;          // 수령자 휴대전화

    @NotBlank(message = "우편번호")
    @Size(min = 5, max = 5, message = "우편번호")
    private String recipientAddressZipCode; // 수령자 우편번호

    @NotBlank(message = "주소")
    private String recipientAddress;        // 수령자 주소

    @NotBlank(message = "상세주소")
    @Size(min = 2, max = 100, message = "상세주소")
    private String recipientAddressDetail;  // 수령자 상세주소

    @Size(min = 1, max = 2000, message = "주문메모")
    private String orderMemo;               // 주문 메모

    private String partnerCode;             // 파트너 코드
    @JsonIgnore
    private String userId;                  // 사용자 아이디
    @JsonIgnore
    private boolean isInvoiceNo;            // 택배 송장번호 등록 여부

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
    public void setAopUserId(String userId) {
        this.userId = userId;
    }
}
