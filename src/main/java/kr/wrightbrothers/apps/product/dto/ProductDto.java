package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProductDto {

    @Getter @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Product {
        /** 대 카테고리 코드 */
        @NotBlank(message = "대 카테고리")
        private String categoryOneCode;

        /** 대 카테고리 이름 */
        @NotBlank(message = "대 카테고리")
        private String categoryOneName;

        /** 중 카테고리 코드 */
        @NotBlank(message = "중 카테고리")
        private String categoryTwoCode;

        /** 중 카테고리 이름 */
        @NotBlank(message = "중 카테고리")
        private String categoryTwoName;

        /** 소 카테고리 코드 */
        @NotBlank(message = "소 카테고리")
        private String categoryThrCode;

        /** 소 카테고리 이름 */
        @NotBlank(message = "소 카테고리")
        private String categoryThrName;

        /** 싱픔먕 */
        @NotBlank(message = "상품명")
        @Size(min = 2, max = 50, message = "상품명")
        private String productName;

        /** 브랜드 번호 */
        private String brandNo;

        /** 브랜드 이름 */
        @NotBlank(message = "브랜드")
        private String brandName;

        /** 모델 코드 */
        private String modelCode;

        /** 모델 이름 */
        private String modelName;

        /** 모델 연식 */
        private String modelYear;

        /** 유튜브 주소 */
        @Size(min = 0, max = 200, message = "Youtube Url")
        private String youtubeUrl;

        /** 상품 바코드 */
        @Size(min = 0, max = 200, message = "상품 바코드")
        private String productBarcode;

        /** 상품 이미지 파일 번호 */
        @NotBlank(message = "상품 이미지")
        private String productFileNo;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Product {
        /** 파트너 코드 */
        private String partnerCode;

        /** 상품 코드 */
        private String productCode;

        /** 상품 유형 */
        @JsonIgnore
        private String productType;

        /** 작성자 아이디 */
        @JsonIgnore
        private String userId;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResBody extends Product {
        /** 상품 유형 */
        private String productType;

        /** 상품 코드 */
        private String productCode;
    }
}
