package kr.wrightbrothers.apps.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.Assert;

import javax.validation.constraints.*;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserInsertDto {
    @ApiModelProperty(value = "아이디(이메일)", required = true)
    @NotBlank(message = "아이디(이메일)")
    @Size(min = 10, max = 50, message = "아이디(이메일)")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "아이디(이메일) 형식이 맞지 않습니다.")
    private String userId;

    @ApiModelProperty(value = "비밀번호", required = true)
    @NotBlank(message = "비밀번호")
    @Size(min = 10, max = 20, message = "비밀번호")
    @Pattern(regexp = "^[A-Za-z0-9]{10,20}$", message = "비밀번호는 숫자,영문 조합 10~20자리만 입력 가능 합니다.")
    private String userPwd;

    @ApiModelProperty(value = "이름", required = true)
    @NotBlank(message = "이름")
    @Size(min = 2, max = 20, message = "이름")
    private String userName;

    @ApiModelProperty(value = "휴대전화 번호", required = true)
    @Size(min = 10, max = 11, message = "휴대전화 번호")
    @NotBlank(message = "휴대전화 번호")
    private String userPhone;

    @ApiModelProperty(value = "이메일 인증여부", required = true)
    @NotNull(message = "이메일 인증여부")
    @AssertTrue(message = "이메일 인증여부")
    private boolean isAuthEmail;

    @ApiModelProperty(value = "이용약관 동의 여부", required = true)
    @NotNull(message = "이용약관 동의 여부")
    @AssertTrue(message = "이용약관 동의 여부")
    private boolean termsAgreedFlag;

    @ApiModelProperty(value = "개인정보 수집 및 이용 동의 여부", required = true)
    @NotNull(message = "개인정보 수집 및 이용 동의 여부")
    @AssertTrue(message = "개인정보 수집 및 이용 동의 여부")
    private boolean collectionAgreedFlag;

    @ApiModelProperty(value = "프로모션 정보 수신 동의 여부", required = true)
    @NotNull(message = "프로모션 정보 수신 동의 여부")
    private boolean promotionAgreedFlag;

    @JsonIgnore
    @ApiModelProperty(value = "계정 상태")
    private String userStatusCode;

    public void changeUserStatusCode(String userStatucCode) {
        this.userStatusCode = userStatucCode;
    }
    public void changePwd(String userPwd){
        Assert.hasText(userPwd, "비밀번호가 존재하지 않습니다.");
        this.userPwd = userPwd;
    }
}
