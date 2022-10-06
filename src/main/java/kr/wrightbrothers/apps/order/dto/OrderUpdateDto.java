package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@ApiModel(value = "주문내역 수정 데이터")
public class OrderUpdateDto {
    @ApiModelProperty(value = "주문 번호", required = true)
    @NotBlank(message = "주문 번호")
    private String orderNo;                 // 주문 번호

    @ApiModelProperty(value = "주문 번호", required = true)
    @NotBlank(message = "수령자명")
    @Size(min = 2, max = 20, message = "수령자명")
    private String recipientName;           // 수령자 이름

    @ApiModelProperty(value = "휴대전화", required = true)
    @NotBlank(message = "휴대전화")
    @Size(min = 10, max = 11, message = "휴대전화")
    @Pattern(regexp = "^\\d+$", message = "휴대전화는 숫자만 입력 가능 합니다.")
    private String recipientPhone;          // 수령자 휴대전화

    @ApiModelProperty(value = "우편번호", required = true)
    @NotBlank(message = "우편번호")
    @Size(min = 5, max = 5, message = "우편번호")
    private String recipientAddressZipCode; // 수령자 우편번호

    @ApiModelProperty(value = "주소", required = true)
    @NotBlank(message = "주소")
    private String recipientAddress;        // 수령자 주소

    @ApiModelProperty(value = "상세주소", required = true)
    @NotBlank(message = "상세주소")
    @Size(min = 2, max = 100, message = "상세주소")
    private String recipientAddressDetail;  // 수령자 상세주소

    @ApiModelProperty(value = "주문메모", required = false)
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

    @Getter
    @Builder
    public static class Status {
        private String orderNo;                 // 주문 번호
        private String orderStatusCode;         // 주문 진행 코드
        private String userId;                  // 사용자 아이디
    }

}
