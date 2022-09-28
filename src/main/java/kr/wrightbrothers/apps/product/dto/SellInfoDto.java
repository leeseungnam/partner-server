package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SellInfoDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SellInfo {
        @ApiModelProperty(value = "상품금액", required = true)
        @Max(value = 100000000, message = "상품금액")
        @Min(value = 100, message = "상품금액")
        @NotNull(message = "상품금액")
        private Long productAmount;

        @ApiModelProperty(value = "할인여부", required = true)
        @NotBlank(message = "할인여부")
        private String discountFlag;

        @ApiModelProperty(value = "할인구분")
        private String discountType;

        @ApiModelProperty(value = "할인금액")
        private String discountAmount;

        @ApiModelProperty(value = "전시상태", required = true)
        @NotBlank(message = "전시상태")
        private String displayFlag;

        @ApiModelProperty(value = "옵션여부", required = true)
        @NotBlank(message = "옵션여부")
        private String productOptionFlag;

        @ApiModelProperty(value = "공금금액")
        @Max(value = 100000000, message = "공급금액")
        private Long supplyAmount;

        @ApiModelProperty(value = "판매가", required = true)
        @NotNull(message = "판매가")
        private Long finalSellAmount;

        @ApiModelProperty(value = "싱픔싱테", required = true)
        @NotBlank(message = "상품상태")
        private String productStatusCode;

        @ApiModelProperty(value = "재고", required = true)
        @NotNull(message = "재고")
        @Min(value = 1, message = "재고")
        @Max(value = 9999, message = "재고")
        private int productStockQty;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @ApiModel(value = "상품 판매 정보")
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
