package kr.wrightbrothers.apps.partner.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

public class PartnerRejectDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartnerReject {

        @ApiModelProperty(value = "파트너 상태")
        @NotNull(message = "파트너 상태")
        private String partnerStatus;

        @ApiModelProperty(value = "심사 반려 사유")
        @NotNull(message = "심사 반려 사유")
        private String rejectComment;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Param extends PartnerReject{
        @ApiModelProperty(value = "파트너 코드")
        private String partnerCode;

    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResBody extends PartnerReject{

        @ApiModelProperty(value = "생성일")
        private String createDate;

    }
}
