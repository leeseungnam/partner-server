package kr.wrightbrothers.apps.sign.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ApiModel(value = "로그인 요청 데이터")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInDto {

    @ApiModelProperty(value = "아이디(이메일)", required = true)
    @NotBlank(message = "아이디(이메일)")
//    @Size(min = 10, max = 50, message = "아이디(이메일)")
//    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "아이디(이메일) 형식이 맞지 않습니다.")
    private String userId;

    @ApiModelProperty(value = "비밀번호", required = true)
    @NotBlank(message = "비밀번호")
//    @Size(min = 10, max = 20, message = "비밀번호")
//    @Pattern(regexp = "^[A-Za-z0-9]{10,20}$", message = "비밀번호는 숫자,영문 조합 10~20자리만 입력 가능 합니다.")
    private String userPwd;

}
