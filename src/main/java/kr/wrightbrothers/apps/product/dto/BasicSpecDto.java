package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

public class BasicSpecDto {

    @Getter @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class BasicSpec {
        /** 완차구분 */
        private String salesCategoryCode;

        /** 구동계 */
        private String drivetrainTypeCode;

        /** 프레임 소재 */
        private String frameMaterialCode;

        /** 프레임 사이즈 */
        private String frameSizeCode;

        /** 브레이크 타입 */
        private String brakeTypeCode;

        /** 용도 테마 */
        private String purposeThemeCode;

        /** 휠 사이즈 */
        private String wheelSizeCode;

        /** 서스펜션 */
        private String suspensionTypeCode;

        /** 호환키(최소) */
        private String minHeightPerson;

        /** 호환키(최대) */
        private String maxHeightPerson;

        /** 무게 */
        private String bikeWeight;

        /** 탑승 연령대 */
        private List<String> ageList;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends BasicSpec {
        /** 상품 코드 */
        @JsonIgnore
        private String productCode;

        /** 사용자 아이디 */
        @JsonIgnore
        private String userId;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends BasicSpec {}
}
