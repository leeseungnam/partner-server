package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

public class GuideDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Guide {
        @NotBlank(message = "안내 사항")
        private String productGuide;
        @NotBlank(message = "배송 안내")
        private String deliveryGuide;
        @NotBlank(message = "교환/반품 안내")
        private String exchangeReturnGuide;
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
