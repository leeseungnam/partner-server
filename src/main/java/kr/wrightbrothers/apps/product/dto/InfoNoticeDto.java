package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class InfoNoticeDto {

    @Getter @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class InfoNotice {
        /** 싱픔 구분 */
        @NotBlank(message = "상품구분")
        private String categoryCode;

        /** 정보고시 가변필드 1 */
        private String productAttribute1;

        /** 정보고시 가변필드 2 */
        private String productAttribute2;

        /** 정보고시 가변필드 3 */
        private String productAttribute3;

        /** 정보고시 가변필드 4 */
        private String productAttribute4;

        /** 정보고시 가변필드 5 */
        private String productAttribute5;

        /** 정보고시 가변필드 6 */
        private String productAttribute6;

        /** 정보고시 가변필드 7 */
        private String productAttribute7;

        /** 정보고시 가변필드 8 */
        private String productAttribute8;

        /** 정보고시 가변필드 9 */
        private String productAttribute9;

        /** 정보고시 가변필드 10 */
        private String productAttribute10;

        /** 정보고시 가변필드 11 */
        private String productAttribute11;

        /** 정보고시 가변필드 12 */
        private String productAttribute12;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends InfoNotice {
        /** 상품 코드 */
        @JsonIgnore
        private String productCode;

        /** 작성자 아이디 */
        @JsonIgnore
        private String userId;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends InfoNotice {}

}
