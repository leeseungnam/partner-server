package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class OptionDto {

    @Data
    @Jacksonized
    @SuperBuilder
    public static class Option {
        private int optionSeq;
        private String optionName;
        private String optionValue;
        private String optionSurcharge;
        private Long optionStockQty;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Option {
        @JsonIgnore
        private String productCode;
        @JsonIgnore
        private String userId;
    }
}
