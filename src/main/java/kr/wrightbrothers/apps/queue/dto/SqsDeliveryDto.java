package kr.wrightbrothers.apps.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqsDeliveryDto {
    @JsonProperty("ProductDeliveryCode")
    private String deliveryType;            // 배송코드

    @JsonProperty("DeliveryBundleFlag")
    private String deliveryBundleFlag;      // 묶음 배송 여부

    @JsonProperty("ChargeType")
    private String chargeType;              // 배송비 설정

    @JsonProperty("ChargeBase")
    private Integer chargeBase;             // 기본 배송비

    @JsonProperty("TermsFreeCharge")
    private Long termsFreeCharge;           // 배송비 무료 기준 요금

    @JsonProperty("PaymentType")
    private String paymentType;             // 결제 방식

    @JsonProperty("SurchargeFlag")
    private String surchargeFlag;           // 제주 / 도서산간 추가 배송비 여부

    @JsonProperty("AreaCode")
    private String areaCode;                // 권역 코드

    @JsonProperty("SurchargeJejudo")
    private Integer surchargeJejudo;        // 제주도 추가 요금

    @JsonProperty("SurchargeIsolated")
    private Integer surchargeIsolated;      // 도서산간 추가 요금

    @JsonProperty("UnstoringZipCode")
    private String unstoringZipCode;        // 출고지 우편번호

    @JsonProperty("UnstoringAddress")
    private String unstoringAddress;        // 출고지 주소

    @JsonProperty("UnstoringAddressDetail")
    private String unstoringAddressDetail;  // 출고지 상세주소

    @JsonProperty("ReturnZipCode")
    private String returnZipCode;           // 반품지 우편번호

    @JsonProperty("ReturnAddress")
    private String returnAddress;           // 반품지 주소

    @JsonProperty("ReturnAddressDetail")
    private String returnAddressDetail;     // 반품지 상세주소

    @JsonProperty("ExchangeCharge")
    private Integer exchangeCharge;         // 교환 배송 요금

    @JsonProperty("ReturnCharge")
    private Integer returnCharge;           // 반품 배송 요금

    @JsonProperty("ReturnDeliveryCompanyCode")
    private String returnDeliveryCompanyCode;   // 반품 / 교환 택배사 코드
}
