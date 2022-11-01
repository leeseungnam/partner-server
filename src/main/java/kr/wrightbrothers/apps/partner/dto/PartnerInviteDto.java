package kr.wrightbrothers.apps.partner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import springfox.documentation.annotations.ApiIgnore;

public class PartnerInviteDto {
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

        @ApiIgnore
        public void changeAuthCode(String authCode) {
            this.authCode = authCode;
        }
        @ApiIgnore
        public void changePartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }

    }
    @ApiModel(value = "파트너 운영자 파라미터 정보")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Param extends PartnerOperator{

        @ApiModelProperty(value = "운영자 초대 코드")
        private String inviteCode;

        @ApiModelProperty(value = "운영자 초대 상태 0:미수락, 1:수락")
        private String inviteStatus;

        @ApiModelProperty(value = "운영자 초대 보낸 사람")
        private String userId;

    }
    @ApiModel(value = "파트너 운영자 요청 정보")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody extends PartnerOperator{
        @ApiModelProperty(value = "운영자 초대 코드")
        @JsonIgnore
        private String inviteCode;

        @ApiModelProperty(value = "운영자 초대 받은 사람")
        private String inviteReceiver;

        @ApiModelProperty(value = "운영자 초대 보낸 사람")
        private String inviteSender;

        @ApiModelProperty(value = "운영자 초대 보낸 사람")
        @JsonIgnore
        private String userId;

        @ApiModelProperty(value = "운영자 초대 상태 0:미수락, 1:수락")
        @JsonIgnore
        private String inviteStatus;

        @ApiIgnore
        public void changeInviteCode(String inviteCode) {
            this.inviteCode = inviteCode;
        }

        @ApiIgnore
        public void changeInviteStatus(String inviteStatus) {
            this.inviteStatus = inviteStatus;
        }

        @ApiIgnore
        public void changeUserId(String userId) {
            this.userId = userId;
        }

    }

    @ApiModel(value = "파트너 운영자 응답 정보")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResBody extends PartnerOperator{
        @ApiModelProperty(value = "운영자 초대 코드")
        private String inviteCode;

        @ApiModelProperty(value = "운영자 아이디")
        private String userId;

        @ApiModelProperty(value = "운영자명")
        private String userName;

        @ApiModelProperty(value = "운영자 초대 상태 0:미수락, 1:수락")
        private String inviteStatus;

        @ApiModelProperty(value = "운영자 초대 받은 사람")
        private String inviteReceiver;

        @ApiModelProperty(value = "운영자 초대 보낸 사람")
        private String inviteSender;
    }
}
