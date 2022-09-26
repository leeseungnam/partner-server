package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class GuideDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Guide {
        @ApiModelProperty(value = "안내 사항")
        @NotBlank(message = "안내 사항")
        @Size(min = 30, max = 2000, message = "안내 사항")
        private String productGuide;
        @ApiModelProperty(value = "배송 안내")
        @NotBlank(message = "배송 안내")
        @Size(min = 30, max = 2000, message = "배송 안내")
        private String deliveryGuide;
        @ApiModelProperty(value = "교환/반품 안내")
        @NotBlank(message = "교환/반품 안내")
        @Size(min = 30, max = 2000, message = "교환/반품 안내")
        private String exchangeReturnGuide;
        @ApiModelProperty(value = "A/S 안내")
        @NotBlank(message = "A/S 안내")
        private String asGuide;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Guide {
        @JsonIgnore
        private String productCode;
        @JsonIgnore
        private String userId;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends Guide {}

}
