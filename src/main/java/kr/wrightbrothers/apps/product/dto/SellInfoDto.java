package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class SellInfoDto {

    @Data
    @Jacksonized
    @SuperBuilder
    public static class SellInfo {
        private Long productAmount;
        private String discountFlag;
        private String discountType;
        private String discountAmount;
        private String displayFlag;
        private Long finalSellAmount;
        private String productStatusCode;
        private int productStockQty;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends SellInfo {
        @JsonIgnore
        private String productCode;
        @JsonIgnore
        private String userId;
    }
}
