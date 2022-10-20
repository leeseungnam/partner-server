package kr.wrightbrothers.apps.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqsInfoNoticeDto {
    @JsonProperty("CategoryCode")
    private String categoryCode;    // 카테고리 코드

    @JsonProperty("ProductAttribute1")
    private String modelName;       // 품명/모데리명

    @JsonProperty("ProductAttribute2")
    private String productSize;     // 크기

    @JsonProperty("ProductAttribute3")
    private String productWeight;   // 중량

    @JsonProperty("ProductAttribute4")
    private String productMaterial; // 재질

    @JsonProperty("ProductAttribute5")
    private String productComponent;// 제품구성

    @JsonProperty("ProductAttribute6")
    private String modelYear;       // 출시연도

    @JsonProperty("ProductAttribute7")
    private String modelMonth;      // 출시월

    @JsonProperty("ProductAttribute8")
    private String productMfr;      // 제조자(사)

    @JsonProperty("ProductAttribute9")
    private String detailSpec;      // 세부사양

    @JsonProperty("ProductAttribute10")
    private String qaStandard;      // 품질보증기준

    @JsonProperty("ProductAttribute11")
    private String asPhone;         // AS 연락처
}
