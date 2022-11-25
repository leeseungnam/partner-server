package kr.wrightbrothers.apps.noti.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "로그인 요청 데이터")
public class AuthPhoneDto {

    @Getter
    @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @ApiModel(value = "휴대폰번호 인증 데이터")
    public static class AuthPhone {
        @ApiModelProperty(value = "휴대폰번호", required = true)
        @NotBlank(message = "휴대폰번호")
        private String phone;
    }

    @ApiModel(value = "휴대폰번호 인증 요청 데이터")
    @Getter
    @Setter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ReqBody extends AuthPhone {

    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "휴대폰번호 인증 응답 데이터")
    public static class ResBody extends AuthPhone {
        @ApiModelProperty(value = "인증코드", required = true)
        @NotBlank(message = "인증코드")
        private String authCode;
    }
}
