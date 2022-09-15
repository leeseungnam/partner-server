package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

public class BasicSpecDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
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
        private List<String> ageList;
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

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends BasicSpec {}
}
