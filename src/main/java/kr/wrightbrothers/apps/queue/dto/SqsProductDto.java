package kr.wrightbrothers.apps.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqsProductDto {
    @JsonProperty("PurchaseRequestNumber")
    private String partnerCode;         // 파트너 코드

    @JsonProperty("ProductCode")
    private String productCode;         // 상품 코드

    @JsonProperty("ProductType")
    private String productType;         // 상품 유형

    @JsonProperty("CategoryDepthOne")
    private String categoryOneCode;     // 대 카테고리 코드

    private String categoryOneName;     // 대 카테고리 이름

    @JsonProperty("CategoryDepthTwo")
    private String categoryTwoCode;     // 중 카테고리 코드

    private String categoryTwoName;     // 중 카테고리 이름

    @JsonProperty("CategoryDepthThree")
    private String categoryThrCode;     // 소 카테고리 코드

    private String categoryThrName;     // 소 카테고리 이름

    @JsonProperty("ProductName")
    private String productName;         // 상품 이름

    @JsonProperty("BrandNumber")
    private String brandNo;             // 브랜드 번호

    @JsonProperty("BrandName")
    private String brandName;           // 브랜드 이름

    @JsonProperty("ModelNumber")
    private String modelCode;           // 모델 코드

    @JsonProperty("ModelName")
    private String modelName;           // 모델 이름

    @JsonProperty("ModelYear")
    private String modelYear;           // 모델 연식

    @JsonProperty("YoutubeUrl")
    private String youtubeUrl;          // 유튜브 주소

    @JsonProperty("ProductBarcode")
    private String productBarcode;      // 상품 바코드

    @JsonProperty("ProductFileNo")
    private String productFileNo;       // 상품 파일 번호

    private String productDescription;  // 상품 상세 설명

    @JsonProperty("UpdateUserId")
    private String updateUserId;        // 수정자 아이디
}
