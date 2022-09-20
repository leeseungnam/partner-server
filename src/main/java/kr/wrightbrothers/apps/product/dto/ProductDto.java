package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProductDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Product {
        @NotBlank(message = "상품 유형")
        private String productType;         // 상품 유형
        @NotBlank(message = "대 카테고리")
        private String categoryOneCode;     // 대 카테고리 코드
        @NotBlank(message = "대 카테고리")
        private String categoryOneName;     // 대 카테고리 이름
        @NotBlank(message = "중 카테고리")
        private String categoryTwoCode;     // 중 카테고리 코드
        @NotBlank(message = "중 카테고리")
        private String categoryTwoName;     // 중 카테고리 이름
        @NotBlank(message = "소 카테고리")
        private String categoryThrCode;     // 소 카테고리 코드
        @NotBlank(message = "소 카테고리")
        private String categoryThrName;     // 소 카테고리 이름
        @NotBlank(message = "상품 이름")
        @Size(min = 2, max = 50, message = "상품 이름")
        private String productName;         // 상품 이름
        @NotBlank(message = "브랜드")
        private String brandNo;             // 브랜드 번호
        @NotBlank(message = "브랜드")
        private String brandName;           // 브랜드 이름
        private String modelCode;           // 모델 코드
        private String modelName;           // 모델 이름
        private String modelYear;           // 모델 연식
        private String youtubeUrl;          // 유튜브 주소
        @Size(min = 1, max = 200, message = "상품 바코드")
        private String productBarcode;      // 상품 바코드
        @NotBlank(message = "상품 이미지")
        private String productFileNo;       // 상품 파일 번호
        private String productDescription;  // 상품 상세 설명
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Product {
        private String partnerCode;         // 파트너 코드
        private String productCode;         // 상품 코드
        @JsonIgnore
        private String userId;              // 작성자 아이디
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResBody extends Product {
        @NotBlank(message = "상품 코드")
        @Size(min = 10, max = 10, message = "상품 코드")
        private String productCode;
    }
}
