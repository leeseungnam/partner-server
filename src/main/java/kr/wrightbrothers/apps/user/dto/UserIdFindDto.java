package kr.wrightbrothers.apps.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserIdFindDto {

    @ApiModel(value = "회원 아이디 찾기 요청 데이터")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody {
        @ApiModelProperty(value = "이름", required = true)
        @NotBlank(message = "이름")
        @Size(min = 2, max = 20, message = "이름")
        private String userName;

        @ApiModelProperty(value = "휴대전화 번호", required = true)
        @Size(min = 10, max = 11, message = "휴대전화 번호")
        @NotBlank(message = "휴대전화 번호")
        private String userPhone;
    }
}