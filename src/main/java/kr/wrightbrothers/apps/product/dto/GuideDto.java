package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class GuideDto {

    @Data
    @Jacksonized
    @SuperBuilder
    public static class Guide {
        private String productGuide;
        private String deliveryGuide;
        private String exchangeReturnGuide;
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
}
