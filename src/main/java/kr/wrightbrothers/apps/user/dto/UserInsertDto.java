package kr.wrightbrothers.apps.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.Assert;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserInsertDto {
    @ApiModelProperty(value = "아이디(Email)", required = true)
    @NotBlank(message = "아이디(Email)")
    private String userId;

    @ApiModelProperty(value = "비밀번호", required = true)
    @NotBlank(message = "비밀번호")
    @Size(min = 10, max = 20, message = "비밀번호")
    private String userPwd;

    @ApiModelProperty(value = "이름", required = true)
    @NotBlank(message = "이름")
    @Size(min = 1, max = 50, message = "이름")
    private String userName;

    @ApiModelProperty(value = "휴대전화 번호", required = true)
    @NotBlank(message = "휴대전화 번호")
    private String userPhone;

    @ApiModelProperty(value = "이메일인증 여부", required = true)
    @NotNull(message = "이메일인증 여부")
    @AssertTrue
    private boolean isAuthEmail;

    @ApiModelProperty(value = "이용약관 동의 여부", required = true)
    @NotNull(message = "이용약관 동의 여부")
    @AssertTrue
    private boolean termsAgreedFlag;

    @ApiModelProperty(value = "개인정보 수집 및 이용 동의 여부", required = true)
    @NotNull(message = "개인정보 수집 및 이용 동의 여부")
    @AssertTrue
    private boolean collectionAgreedFlag;

    @ApiModelProperty(value = "프로모션 정보 수신 동의 여부", required = true)
    @NotNull(message = "프로모션 정보 수신 동의 여부")
    private boolean promotionAgreedFlag;

    @JsonIgnore
    @ApiModelProperty(value = "계정 상태", required = true)
    @NotNull(message = "계정 상태")
    private String userStatusCode;

    public void changeUserStatusCode(String userStatucCode) {
        this.userStatusCode = userStatucCode;
    }
    public void changePwd(String userPwd){
        Assert.hasText(userPwd, "비밀번호가 존재하지 않습니다.");
        this.userPwd = userPwd;
    }
}
