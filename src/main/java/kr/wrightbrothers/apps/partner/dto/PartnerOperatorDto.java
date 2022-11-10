package kr.wrightbrothers.apps.partner.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class PartnerOperatorDto {
    @ApiModel(value = "파트너 운영자 정보")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartnerOperator {

        @ApiModelProperty(value = "권한 코드")
        private String authCode;

        @ApiModelProperty(value = "파트너 코드")
        private String partnerCode;

    }

    @ApiModel(value = "파트너 운영자 요청 정보")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody extends PartnerOperator{
        @ApiModelProperty(value = "운영자 아이디")
        private String userId;

    }

    @ApiModel(value = "파트너 운영자 응답 정보")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResBody extends PartnerOperator{
        @ApiModelProperty(value = "운영자 아이디")
        private String userId;

        @ApiModelProperty(value = "운영자명")
        private String userName;

        @ApiModelProperty(value = "권한 코드 명")
        private String authCodeName;

        public void changeAuthCodeName(String authCodeName){
            this.authCodeName = authCodeName;
        }
    }
}
