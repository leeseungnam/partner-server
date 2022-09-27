package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class OptionDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        @ApiModelProperty(value = "옵션 번호")
        private int optionSeq;

        @ApiModelProperty(value = "옵션 명")
        private String optionName;

        @ApiModelProperty(value = "옵션 항목")
        private String optionValue;

        @ApiModelProperty(value = "변동 금액")
        private Long optionSurcharge;

        @ApiModelProperty(value = "옵션 재고 수량")
        private int optionStockQty;
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
