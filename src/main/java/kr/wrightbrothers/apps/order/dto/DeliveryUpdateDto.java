package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryUpdateDto {
    @ApiModelProperty(value = "주문 번호", required = true)
    @NotBlank(message = "주문 번호")
    private String orderNo;

    @ApiModelProperty(value = "주문 상품 SEQ", required = true)
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray; // 주문 상품 SEQ Array

    @ApiModelProperty(value = "수령자명", required = true)
    @NotBlank(message = "수령자명")
    private String recipientName;           // 수령자 명

    @ApiModelProperty(value = "수령자 연락처", required = true)
    @NotBlank(message = "수령자 연락처")
    private String recipientPhone;          // 수령자 연락처

    @ApiModelProperty(value = "수령자 우편번호", required = true)
    @NotBlank(message = "수령자 우편번호")
    private String recipientAddressZipCode; // 수령자 우편번호

    @ApiModelProperty(value = "수령자 주소", required = true)
    @NotBlank(message = "수령자 주소")
    private String recipientAddress;        // 수령자 주소

    @ApiModelProperty(value = "수령자 상세주소")
    private String recipientAddressDetail;  // 수령자 상세주소

    private String partnerCode;             // 파트너 코드
    @JsonIgnore
    private String userId;                  // 사용자 아이디

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setAopUserId(String userId) {
        this.userId = userId;
    }
}
