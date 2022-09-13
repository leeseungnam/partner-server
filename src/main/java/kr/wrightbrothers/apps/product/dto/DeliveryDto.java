package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class DeliveryDto {

    @Data
    @Jacksonized
    @SuperBuilder
    public static class Delivery {
        private String deliveryType;
        private String visitFlag;
        private String quickServiceFlag;
        private String deliveryBundleFlag;
        private String chargeType;
        private String chargeBase;
        private String termsFreeCharge;
        private String paymentType;
        private String surchargeFlag;
        private String areaCode;
        private String surchargeJejudo;
        private String surchargeIsolated;
        private String unstoringAddress;
        private String returnAddress;
        private String returnCharge;
        private String returnDeliveryCompany;
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

}
