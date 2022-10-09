package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInvoiceUpdateDto {
    @NotBlank(message = "주문 번호")
    private String orderNo;

    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeq;      // 주문 상품 SEQ

    @NotBlank(message = "택배사 코드")
    private String deliveryCompanyCode;     // 택배사 코드

    @NotBlank(message = "택배사 이름")
    private String deliveryCompanyName;     // 택배사 이름

    @NotBlank(message = "송장번호")
    @Size(min = 2, max = 50, message = "송장번호")
    @Pattern(regexp = "^\\d+$", message = "송장번호는 숫자만 입력 가능 합니다.")
    private String invoiceNo;               // 택배 송장번호

    private String partnerCode;             // 파트너 코드
    @JsonIgnore
    private String userId;                  // 사용자 아이디

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setAopUserId(String userId) {
        this.userId = userId;
    }

    // 송장번호 입력에 따른 DTO 객체 변환
    public DeliveryInvoiceUpdateDto.ProductInvoice toProductInvoiceDto(Integer orderProductSeq) {
        return DeliveryInvoiceUpdateDto.ProductInvoice.builder()
                .orderNo(this.orderNo)
                .orderProductSeq(orderProductSeq)
                .deliveryCompanyCode(this.deliveryCompanyCode)
                .deliveryCompanyName(this.deliveryCompanyName)
                .invoiceNo(this.invoiceNo)
                .userId(this.userId)
                .build();
    }

    @Getter
    @Builder
    public static class ProductInvoice {
        private String orderNo;
        private Integer orderProductSeq;
        private String deliveryCompanyCode;
        private String deliveryCompanyName;
        private String invoiceNo;
        private String userId;
    }

}
