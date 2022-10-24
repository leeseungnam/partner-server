package kr.wrightbrothers.apps.partner.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PartnerAndAuthFindDto {
    @Getter
    @Builder
    public static class Param {
        private String userId;
    }

    @Setter
    @Getter
    @Builder
    public static class ResBody {
        @ApiModelProperty(value = "스토어명")
        private String partnerName;

        @ApiModelProperty(value = "입점사타입 공통코드: 000047(1: 일반입점, 2: 재생입점)")
        private String partnerKind;

        @ApiModelProperty(value = "파트너 코드")
        private String partnerCode;

        @ApiModelProperty(value = "파트너 상태 코드")
        private String partnerStatus;

        @ApiModelProperty(value = "권한 코드")
        private String authCode;

        @ApiModelProperty(value = "계약 상태 코드")
        private String contractStatus;

        @ApiModelProperty(value = "파트너 권한에 따른 문구")
        private String comment;

        @ApiModelProperty(value = "파트너 상태 코드명")
        private String partnerStatusName;

        @ApiModelProperty(value = "계약 상태 코드명")
        private String contractStatusName;

        @ApiModelProperty(value = "권한 코드명")
        private String authCodeName;
    }
}
