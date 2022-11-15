package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
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
    @NotBlank(message = "주문 번호")
    private String orderNo;                 // 주문 번호

    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray; // 주문 상품 SEQ

    private String deliveryCompanyCode;     // 택배 회사 코드
    private String invoiceNo;               // 운송장 번호
    private String recipientName;           // 수령자 이름
    private String recipientPhone;          // 수령자 연락처
    private String recipientAddressZipCode; // 수령자 우편번호
    private String recipientAddress;        // 수령자 주소
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

    public RequestReturnUpdateDto toRequestReturnUpdateDto() {
        return RequestReturnUpdateDto.builder()
                .orderNo(this.orderNo)
                .orderProductSeqArray(this.orderProductSeqArray)
                .returnProcessCode(OrderProductStatusCode.START_RETURN.getCode())
                .requestCode(this.deliveryCompanyCode)
                .requestValue(this.invoiceNo)
                .recipientName(this.recipientName)
                .recipientPhone(this.recipientPhone)
                .recipientAddressZipCode(this.recipientAddressZipCode)
                .recipientAddress(this.recipientAddress)
                .recipientAddressDetail(this.recipientAddressDetail)
                .partnerCode(this.partnerCode)
                .userId(this.userId)
                .build();
    }
}
