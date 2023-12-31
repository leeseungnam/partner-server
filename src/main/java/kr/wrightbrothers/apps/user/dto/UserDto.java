package kr.wrightbrothers.apps.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ApiModel(value = "회원정보 데이터")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends UserAuthDto {
    private String userId;
    private String userPwd;
    private String userName;
    private String userPhone;
    private boolean changePwdFlag;

    public void changePwd(String userPwd) {
        this.userPwd = userPwd;
    }
}