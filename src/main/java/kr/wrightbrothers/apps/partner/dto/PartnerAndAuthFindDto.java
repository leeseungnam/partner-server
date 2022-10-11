package kr.wrightbrothers.apps.partner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PartnerAndAuthFindDto {
    @Getter
    @Builder
    public static class Param {
        private String userId;
    }

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

        @ApiModelProperty(value = "파트너 상태 코드")
        private String authCode;

        @ApiModelProperty(value = "파트너 상태 코드")
        private String contractStatus;

    }
}
