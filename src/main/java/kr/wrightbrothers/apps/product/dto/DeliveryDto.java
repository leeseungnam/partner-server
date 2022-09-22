package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DeliveryDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Delivery {
        @NotBlank(message = "배송방법")
        private String deliveryType;
        @NotBlank(message = "묶음배송")
        private String deliveryBundleFlag;
        @NotBlank(message = "배송비 설정")
        private String chargeType;
        @NotNull(message = "기본 배송비")
        @Max(value = 100000000, message = "기본 배송비")
        private Integer chargeBase;
        private Long termsFreeCharge;
        @NotBlank(message = "결제방식")
        private String paymentType;
        @NotBlank(message = "제주/도서산간 추가 배송비")
        private String surchargeFlag;
        private String areaCode;
        private Integer surchargeJejudo;
        private Integer surchargeIsolated;
        @NotBlank(message = "출고지")
        private String unstoringAddress;
        @NotBlank(message = "반품지")
        private String returnAddress;
        @NotNull(message = "교환배송비")
        @Max(value = 10000000, message = "교환배송비")
        private Integer exchangeCharge;
        @NotNull(message = "반품배송비(편도)")
        @Max(value = 10000000, message = "반품배송비(편도)")
        private Integer returnCharge;
        @NotBlank(message = "반품/교환 택배사")
        private String returnDeliveryCompanyCode;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Delivery {
        @JsonIgnore
        private String productCode;
        @JsonIgnore
        private String userId;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends Delivery {}

}
