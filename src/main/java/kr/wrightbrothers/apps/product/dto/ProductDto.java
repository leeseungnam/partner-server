package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProductDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Product {
        @ApiModelProperty(value = "상품 유형", required = true)
        @NotBlank(message = "상품 유형")
        private String productType;         // 상품 유형

        @ApiModelProperty(value = "대 카테고리 코드", required = true)
        @NotBlank(message = "대 카테고리")
        private String categoryOneCode;     // 대 카테고리 코드

        @ApiModelProperty(value = "대 카테고리 이름", required = true)
        @NotBlank(message = "대 카테고리")
        private String categoryOneName;     // 대 카테고리 이름

        @ApiModelProperty(value = "중 카테고리 코드", required = true)
        @NotBlank(message = "중 카테고리")
        private String categoryTwoCode;     // 중 카테고리 코드

        @ApiModelProperty(value = "중 카테고리 이름", required = true)
        @NotBlank(message = "중 카테고리")
        private String categoryTwoName;     // 중 카테고리 이름

        @ApiModelProperty(value = "소 카테고리 코드", required = true)
        @NotBlank(message = "소 카테고리")
        private String categoryThrCode;     // 소 카테고리 코드

        @ApiModelProperty(value = "소 카테고리 이름", required = true)
        @NotBlank(message = "소 카테고리")
        private String categoryThrName;     // 소 카테고리 이름

        @ApiModelProperty(value = "상품명", required = true)
        @NotBlank(message = "상품명")
        @Size(min = 2, max = 50, message = "상품명")
        private String productName;         // 상품 이름

        @ApiModelProperty(value = "브랜드 번호", required = true)
        @NotBlank(message = "브랜드")
        private String brandNo;             // 브랜드 번호

        @ApiModelProperty(value = "브랜드 이름", required = true)
        @NotBlank(message = "브랜드")
        private String brandName;           // 브랜드 이름

        @ApiModelProperty(value = "모델 코드")
        private String modelCode;           // 모델 코드

        @ApiModelProperty(value = "모델 이름")
        private String modelName;           // 모델 이름

        @ApiModelProperty(value = "모델 연식")
        private String modelYear;           // 모델 연식

        @ApiModelProperty(value = "유튜브 주소")
        @Size(min = 1, max = 200, message = "Youtube Url")
        private String youtubeUrl;          // 유튜브 주소

        @ApiModelProperty(value = "상품 바코드")
        @Size(min = 1, max = 200, message = "상품 바코드")
        private String productBarcode;      // 상품 바코드

        @ApiModelProperty(value = "상품 이미지 파일 번호", required = true)
        @NotBlank(message = "상품 이미지")
        private String productFileNo;       // 상품 파일 번호

        @ApiModelProperty(value = "상품 상세 설명", required = true)
        @NotBlank(message = "상품 상세설명")
        private String productDescription;  // 상품 상세 설명
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @ApiModel(value = "상품 기본 정보")
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
        private String productCode;
    }
}
