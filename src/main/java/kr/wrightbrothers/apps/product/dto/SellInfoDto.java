package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SellInfoDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SellInfo {
        @NotNull(message = "상품금액")
        private Long productAmount;
        @NotBlank(message = "할인여부")
        private String discountFlag;
        private String discountType;
        private String discountAmount;
        @NotBlank(message = "전시상태")
        private String displayFlag;
        @NotBlank(message = "옵션여부")
        private String productOptionFlag;
        private Long supplyAmount;
        @NotNull(message = "판매가")
        private Long finalSellAmount;
        @NotBlank(message = "상품상태")
        private String productStatusCode;
        @Min(value = 1, message = "재고")
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

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends SellInfo {}
}
