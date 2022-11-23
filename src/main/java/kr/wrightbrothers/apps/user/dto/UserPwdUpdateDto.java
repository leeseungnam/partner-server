package kr.wrightbrothers.apps.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ApiModel(value = "회원 비밀번호 변경 요청 데이터")
@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserPwdUpdateDto {
    @ApiModelProperty(value = "아이디(이메일)", required = true)
    @NotBlank(message = "아이디(이메일)")
    @Size(max = 50, message = "아이디(이메일)")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "아이디(이메일) 형식이 맞지 않습니다.")
    private String userId;

    @ApiModelProperty(value = "비밀번호", required = true)
    @NotBlank(message = "비밀번호")
    @Size(min = 10, max = 20, message = "비밀번호")
//    @Pattern(regexp = "^[A-Za-z0-9]{10,20}$", message = "비밀번호는 숫자,영문 조합 10~20자리만 입력 가능 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*\\(\\)\\-_=+,.<>/?₩~;\\[\\]:{}\'\"])[A-Za-z\\d!@#$%^&*\\(\\)\\-_=+,.<>/?₩~;\\[\\]:{}\'\"]{10,20}$", message = "비밀번호는 영문, 숫자, 특수문자 조합 10~20자리만 입력 가능 합니다.")
    private String userPwd;

    @JsonIgnore
    private boolean changePwdFlag;  // 비밀번호 변경 여부 (0 : 변경불필요, 1:변경필요)

    @JsonIgnore
    private String updateDate;  // 수정일

    public void changePwd(String userPwd){
        this.userPwd = userPwd;
    }
    public void setChangePwdFlag(boolean changePwdFlag){
        this.changePwdFlag = changePwdFlag;
    }
}
