package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class BasicSpecDto {

    @Data
    @Jacksonized
    @SuperBuilder
    public static class BasicSpec {
        private String salesCategoryCode;
        private String drivetrainTypeCode;
        private String frameMaterialCode;
        private String frameSizeCode;
        private String brakeTypeCode;
        private String purposeThemeCode;
        private String wheelSizeCode;
        private String suspensionTypeCode;
        private String minHeightPerson;
        private String maxHeightPerson;
        private String bikeWeight;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends BasicSpec {
        @JsonIgnore
        private String productCode;
        @JsonIgnore
        private String userId;
    }
}
