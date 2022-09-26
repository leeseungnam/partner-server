package kr.wrightbrothers.apps.auth.dto;

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
public class AuthEmailDto {

    @ApiModelProperty(value = "아이디(Email)", required = true)
    @NotBlank(message = "아이디(Email)")
    private String userId;

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody extends AuthEmailDto{

        @ApiModelProperty(value = "인증 유형 (1:Email)", required = true)
        @NotBlank(message = "인증 유형")
        private String authType;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResBody extends AuthEmailDto{

        @ApiModelProperty(value = "인증 코드", required = true)
        @NotBlank(message = "인증 코드")
        private String authCode;
    }
}
