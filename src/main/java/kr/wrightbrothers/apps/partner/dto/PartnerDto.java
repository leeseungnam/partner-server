package kr.wrightbrothers.apps.partner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PartnerDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "파트너 데이터")
    public static class Partner {
        @ApiModelProperty(value = "스토어명", required = true)
        @NotBlank(message = "스토어명")
        @Size(min = 2, max = 30, message = "스토어명")
        private String partnerName;

        @ApiModelProperty(value = "썸네일")
        private String thumbnail;

        @ApiModelProperty(value = "입점사타입 공통코드: 000047(1: 일반입점, 2: 재생입점)")
        private String partnerKind;

        @ApiModelProperty(value = "사업자 유형 공통코드 : 000048(1: 일반과세자, 2:간이과세자, 3:단위과세자, 4:법인사업자, 5:면세사업자, 6:기타사업자)", required = true)
        @NotBlank(message = "사업자 유형")
        private String businessClassificationCode;

        @ApiModelProperty(value = "상호명", required = true)
        @NotBlank(message = "상호명")
        @Size(min = 2, max = 20, message = "상호명")
        private String businessName;

        @ApiModelProperty(value = "사업자등록번호", required = true)
        @NotBlank(message = "사업자등록번호")
        @Size(min = 10, max = 10, message = "사업자등록번호")
        private String businessNo;

        @ApiModelProperty(value = "업태")
        @Size(min = 2, max = 20, message = "업태")
        private String businessCondition;

        @ApiModelProperty(value = "업종")
        @Size(min = 2, max = 20, message = "업종")
        private String businessType;

        @ApiModelProperty(value = "대표자명", required = true)
        @NotBlank(message = "대표자명")
        @Size(min = 2, max = 20, message = "대표자명")
        private String repName;

        @ApiModelProperty(value = "대표 전화번호", required = true)
        @NotBlank(message = "대표 전화번호")
        @Size(min = 2, max = 20, message = "대표자 전화번호")
        private String repPhone;

        @ApiModelProperty(value = "고객센터 전화번호", required = true)
        @NotBlank(message = "고객센터 전화번호")
        @Size(min = 2, max = 20, message = "고객센터 전화번호")
        private String csPhone;

        @ApiModelProperty(value = "사업자 주소", required = true)
        @NotBlank(message = "사업자 주소")
        @Size(min = 2, max = 20, message = "사업자 주소")
        private String businessAddress;

        @ApiModelProperty(value = "사업자 주소 나머지", required = true)
        @NotBlank(message = "사업자 주소 나머지")
        @Size(min = 2, max = 20, message = "사업자 주소 나머지")
        private String businessAddressSub;

        @ApiModelProperty(value = "사업자 주소 우편번호", required = true)
        @NotBlank(message = "사업자 주소 우편번호")
        @Size(min = 2, max = 7, message = "사업자 주소 우편번호")
        private String businessAddressZipCode;
    }

    @ApiModel(value = "파트너 요청 데이터")
    @Getter
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Partner{
        @ApiModelProperty(value = "작성자 아이디")
        @JsonIgnore
        private String userId;

        @ApiModelProperty(value = "파트너 코드")
        @JsonIgnore
        private String partnerCode;
        @ApiModelProperty(value = "파트너 상태 공통코드:000086(P01:심사중, P02:운영중, P03:심사반려, P04:운영중지)")
        @JsonIgnore
        private String partnerStatus;

        public void changePartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }
        public void changeUserId(String userId) {
            this.userId = userId;
        }
        public void changePartnerStatus(String partnerStatus) {
            this.partnerStatus = partnerStatus;
        }
    }
    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "파트너 응답 데이터")
    public static class ResBody extends Partner{

        @ApiModelProperty(value = "파트너 코드")
        private String partnerCode;

        @ApiModelProperty(value = "파트너 상태 공통코드:000086(P01:심사중, P02:운영중, P03:심사반려, P04:운영중지)")
        private String partnerStatus;

        @ApiModelProperty(value = "파트너 상태명 공통코드:000086(P01:심사중, P02:운영중, P03:심사반려, P04:운영중지)")
        private String partnerStatusName;

        @ApiModelProperty(value = "사업자 유형명 공통코드 : 000048(1: 일반과세자, 2:간이과세자, 3:단위과세자, 4:법인사업자, 5:면세사업자, 6:기타사업자)")
        private String businessClassificationCodeName;

        public void changePartnerStatusName(String partnerStatusName){
            this.partnerStatusName = partnerStatusName;
        }
        public void changeBusinessClassificationCodeName(String businessClassificationCodeName){
            this.businessClassificationCodeName = businessClassificationCodeName;
        }
    }
}
