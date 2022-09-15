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
        @NotBlank
        private String deliveryType;
        @NotBlank
        private String deliveryBundleFlag;
        @NotBlank
        private String chargeType;
        @NotNull
        @Min(value = 100, message = "기본 배송비")
        @Max(value = 100000000, message = "기본 배송비")
        private Integer chargeBase;
        private Long termsFreeCharge;
        @NotBlank
        private String paymentType;
        @NotBlank
        private String surchargeFlag;
        private String areaCode;
        private Integer surchargeJejudo;
        private Integer surchargeIsolated;
        @NotBlank
        private String unstoringAddress;
        @NotBlank
        private String returnAddress;
        @NotNull
        private Integer returnCharge;
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
