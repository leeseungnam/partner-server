package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.wrightbrothers.apps.common.type.ChargeType;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DeliveryDto {

    @Getter
    @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Delivery {
        @ApiModelProperty(value = "배송방법", required = true)
        @NotBlank(message = "배송방법")
        private String deliveryType;

        @ApiModelProperty(value = "묶음배송", required = true)
        @NotBlank(message = "묶음배송")
        private String deliveryBundleFlag;

        @ApiModelProperty(value = "배송비 설정", required = true)
        @NotBlank(message = "배송비 설정")
        private String chargeType;

        @ApiModelProperty(value = "기본 배송비")
        @Max(value = 100000000, message = "기본 배송비")
        private Integer chargeBase;

        @ApiModelProperty(value = "배송비 무료 기준코드")
        private String termsFreeCode;

        @ApiModelProperty(value = "배송비 무료 기준요금")
        private Long termsFreeCharge;

        @ApiModelProperty(value = "결제방식")
        private String paymentType;

        @ApiModelProperty(value = "제주/도서산간 추가 배송비")
        private String surchargeFlag;

        @ApiModelProperty(value = "권역코드")
        private String areaCode;

        @ApiModelProperty(value = "제주도 추가요금")
        private Integer surchargeJejudo;

        @ApiModelProperty(value = "도서산간 추가요금")
        private Integer surchargeIsolated;

        @ApiModelProperty(value = "출고지 우편번호", required = true)
        private String unstoringZipCode;

        @ApiModelProperty(value = "출고지", required = true)
        @NotBlank(message = "출고지")
        private String unstoringAddress;

        @ApiModelProperty(value = "출고지 상세주소")
        private String unstoringAddressDetail;

        @ApiModelProperty(value = "반품지 우편번호", required = true)
        private String returnZipCode;

        @ApiModelProperty(value = "반품지", required = true)
        @NotBlank(message = "반품지")
        private String returnAddress;

        @ApiModelProperty(value = "반품지 상세주소")
        private String returnAddressDetail;

        @ApiModelProperty(value = "교환배송비", required = true)
        @Max(value = 10000000, message = "교환배송비")
        private Integer exchangeCharge;

        @ApiModelProperty(value = "반품배송비(편도)", required = true)
        @Max(value = 10000000, message = "반품배송비(편도)")
        private Integer returnCharge;

        @ApiModelProperty(value = "반품/교환 택배사", required = true)
        private String returnDeliveryCompanyCode;

        public void validDelivery() {
            // 배송정보 유효성 검사
            if (ObjectUtils.isEmpty(this.deliveryType))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"배송방법"});
            if (ObjectUtils.isEmpty(this.deliveryBundleFlag))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"묶음배송"});
            if (ObjectUtils.isEmpty(this.unstoringZipCode))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"출고지 우편번호"});
            if (ObjectUtils.isEmpty(this.unstoringAddress))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"출고지 주소"});
            if (ObjectUtils.isEmpty(this.returnZipCode))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"반품지 우편번호"});
            if (ObjectUtils.isEmpty(this.returnAddress))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"반품지 주소"});
            if (ObjectUtils.isEmpty(this.chargeType))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"배송비 설정"});

            if (!ObjectUtils.isEmpty(this.returnAddressDetail) && this.returnAddressDetail.length() > 100)
                throw new WBBusinessException(ErrorCode.INVALID_TEXT_SIZE.getErrCode(), new String[]{"반품지 상세주소", "0", "100"});
            if (!ObjectUtils.isEmpty(this.unstoringAddressDetail) && this.unstoringAddressDetail.length() > 100)
                throw new WBBusinessException(ErrorCode.INVALID_TEXT_SIZE.getErrCode(), new String[]{"출고지 상세주소", "0", "100"});
            if (this.exchangeCharge > 10000000)
                throw new WBBusinessException(ErrorCode.INVALID_MONEY_MAX.getErrCode(), new String[]{"교환 배송비", "100000000"});
            if (this.returnCharge > 10000000)
                throw new WBBusinessException(ErrorCode.INVALID_MONEY_MAX.getErrCode(), new String[]{"반품 배송비(편도)", "100000000"});

            // 배송비 설정이 무료의 경우 종료
            if (ChargeType.FREE.getType().equals(this.chargeType)) return;

            if (ObjectUtils.isEmpty(this.chargeBase))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"기본 배송비"});
            if (ObjectUtils.isEmpty(this.paymentType))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"결제방식"});
            if (ObjectUtils.isEmpty(this.surchargeFlag))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"제주/도서산간 추가 배송비 여부"});

            // 조건부 무료 경우 배송비 조건 휴요성 체크
            if (ChargeType.TERMS_FREE.getType().equals(this.chargeType)) {
                if (ObjectUtils.isEmpty(this.termsFreeCharge))
                    throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"배송비 조건"});

                if (this.termsFreeCharge > 100000000)
                    throw new WBBusinessException(ErrorCode.INVALID_MONEY_MAX.getErrCode(), new String[]{"배송비 조건", "100000000"});
            }

            // 제주/도서산간 추가 배송비 설정 시 해당 유효성 검사
            if ("Y".equals(this.surchargeFlag)) {
                if (ObjectUtils.isEmpty(this.areaCode))
                    throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"권역 구분"});
                if (ObjectUtils.isEmpty(this.surchargeIsolated))
                    throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"제주 외 도서산간 추가 배송비"});
                if ("AR3".equals(this.areaCode))
                    if (ObjectUtils.isEmpty(this.surchargeJejudo))
                        throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"제주 추가 배송비"});
            }

            // 범위 체크
            if (ObjectUtils.isEmpty(this.chargeBase) && this.chargeBase > 100000000)
                throw new WBBusinessException(ErrorCode.INVALID_MONEY_MAX.getErrCode(), new String[]{"기본 배송비", "100000000"});

        }
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @ApiModel(value = "상품 배송 정보")
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Delivery {
        @JsonIgnore
        private String productCode;
        @JsonIgnore
        private String userId;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends Delivery {}

}
