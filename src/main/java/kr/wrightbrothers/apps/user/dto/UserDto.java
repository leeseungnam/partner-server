package kr.wrightbrothers.apps.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends UserAuthDto {
    private String userId;
    private String userPwd;
    private String userName;
    private String userPhone;

    public void changePwd(String userPwd) {
        this.userPwd = userPwd;
    }
}