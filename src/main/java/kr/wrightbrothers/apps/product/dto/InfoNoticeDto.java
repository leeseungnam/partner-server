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

        /** 픔명/모델명 */
        @NotBlank(message = "품명/모델명")
        private String modelName;

        /** 크기 */
        private String productSize;

        /** 중량 */
        private String productWeight;

        /** 재질 */
        private String productMaterial;

        /** 제품구성 */
        private String productComponent;

        /** 출시 연도 */
        @NotBlank(message = "출시 연도")
        private String modelYear;

        /** 출시 월 */
        @NotBlank(message = "출시 월")
        private String modelMonth;

        /** 제조자(사) */
        @Size(min = 2, max = 50, message = "제조자(사)")
        @NotBlank(message = "제조자(사)")
        private String productMfr;

        /** 세부사양 */
        private String detailSpec;

        /** 품질보증기준 */
        private String qaStandard;

        /** AS 연락처 */
        @Size(min = 2, max = 20, message = "AS 연락처")
        @NotBlank(message = "AS 연락처")
        private String asPhone;
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
