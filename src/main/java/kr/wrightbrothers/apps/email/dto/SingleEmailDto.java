package kr.wrightbrothers.apps.email.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SingleEmailDto {

    @ApiModelProperty(value = "수신 대상 아이디(이메일)", required = true)
    @NotBlank(message = "수신 대상 아이디(이메일)")
    private String userId;

    @JsonIgnore
    @ApiModelProperty(value = "인증 코드")
    private String authCode;

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody extends SingleEmailDto {

        @ApiModelProperty(value = "이메일 유형 (1:이메일 주소 인증, 2:임시 비밀번호 발급, 3:계약갱신 알림 )", required = true)
        @NotBlank(message = "이메일 유형")
        private String emailType;

    }

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResBody extends SingleEmailDto {

    }
}
