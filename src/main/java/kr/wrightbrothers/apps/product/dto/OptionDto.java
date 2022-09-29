package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class OptionDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        @ApiModelProperty(value = "옵션 번호")
        private Integer optionSeq;

        @ApiModelProperty(value = "옵션 명")
        @Size(min = 1, max = 20, message = "옵션 명")
        private String optionName;

        @ApiModelProperty(value = "옵션 항목")
        @Size(min = 1, max = 20, message = "옵션 항목")
        private String optionValue;

        @ApiModelProperty(value = "변동 금액")
        @Min(value = -10000000, message = "변동 금액")
        @Max(value = 10000000, message = "변동 금액")
        private Long optionSurcharge;

        @ApiModelProperty(value = "옵션 재고 수량")
        @Min(value = 1, message = "옵션 재고 수량")
        @Max(value = 9999, message = "옵션 재고 수량")
        private Integer optionStockQty;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @ApiModel(value = "상품 옵션 정보")
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Option {
        @JsonIgnore
        private String productCode;
        @JsonIgnore
        private String userId;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends Option {}
}
