package kr.wrightbrothers.apps.partner.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;

public class PartnerRejectDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartnerReject {

        @ApiModelProperty(value = "계약 상태")
        @NotNull(message = "계약 상태")
        private String contractStatus;

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

        @ApiModelProperty(value = "계약 코드")
        private String contractCode;

        @ApiModelProperty(value = "처리자")
        private String userId;

        @ApiIgnore
        public void changeUserId(String userId) {
            this.userId = userId;
        }
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
