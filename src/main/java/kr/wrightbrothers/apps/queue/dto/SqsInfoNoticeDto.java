package kr.wrightbrothers.apps.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqsInfoNoticeDto {
    @JsonProperty("CategoryCode")
    private String categoryCode;        // 카테고리 코드

    @JsonProperty("ProductAttribute1")
    private String productAttribute1;   // 정보고시 가변필드1

    @JsonProperty("ProductAttribute2")
    private String productAttribute2;   // 정보고시 가변필드2

    @JsonProperty("ProductAttribute3")
    private String productAttribute3;   // 정보고시 가변필드3

    @JsonProperty("ProductAttribute4")
    private String productAttribute4;   // 정보고시 가변필드4

    @JsonProperty("ProductAttribute5")
    private String productAttribute5;   // 정보고시 가변필드5

    @JsonProperty("ProductAttribute6")
    private String productAttribute6;   // 정보고시 가변필드6

    @JsonProperty("ProductAttribute7")
    private String productAttribute7;   // 정보고시 가변필드7

    @JsonProperty("ProductAttribute8")
    private String productAttribute8;   // 정보고시 가변필드8

    @JsonProperty("ProductAttribute9")
    private String productAttribute9;   // 정보고시 가변필드9

    @JsonProperty("ProductAttribute10")
    private String productAttribute10;   // 정보고시 가변필드10

    @JsonProperty("ProductAttribute11")
    private String productAttribute11;   // 정보고시 가변필드11

    @JsonProperty("ProductAttribute12")
    private String productAttribute12;   // 정보고시 가변필드12
}
