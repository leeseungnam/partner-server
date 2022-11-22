package kr.wrightbrothers.apps.email.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SingleEmailDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SingleEmail {
        @ApiModelProperty(value = "아이디(이메일)", required = true)
        @NotBlank(message = "아이디(이메일)")
        @Size(max = 50, message = "아이디(이메일)")
        @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "아이디(이메일) 형식이 맞지 않습니다.")
        private String userId;
    }
    @ApiModel(value = "메인 인증 요청 데이터")
    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody extends SingleEmail {

        @ApiModelProperty(value = "이메일 유형 (1:이메일 주소 인증, 2:임시 비밀번호 발급, 3:계약갱신 알림 )", required = true)
        @NotBlank(message = "이메일 유형")
        private String emailType;

        @JsonIgnore
        @ApiModelProperty(value = "인증 코드")
        private String authCode;

        @JsonIgnore
        @ApiModelProperty(value = "사용자명")
        private String userName;

        public void changeAuthCode(String authCode) {
            this.authCode = authCode;
        }
    }

    @ApiModel(value = "메인 인증 응답 데이터")
    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResBody extends SingleEmail {

        @ApiModelProperty(value = "인증 코드")
        private String authCode;
    }
}
