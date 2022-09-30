package kr.wrightbrothers.apps.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ApiModel(value = "회원 비밀번호 찾기 요청 데이터")
@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserPwdFindDto {

    @ApiModelProperty(value = "이름", required = true)
    @NotBlank(message = "이름")
    @Size(min = 2, max = 20, message = "이름")
    private String userName;

    @ApiModelProperty(value = "휴대전화 번호", required = true)
    @Size(min = 10, max = 11, message = "휴대전화 번호")
    @NotBlank(message = "휴대전화 번호")
    private String userPhone;

    @ApiModelProperty(value = "이메일 유형 (1:이메일 주소 인증, 2:임시 비밀번호 발급, 3:계약갱신 알림 )", required = true)
    @NotBlank(message = "이메일 데이터")
    private SingleEmailDto.ReqBody singleEmail;
}