package kr.wrightbrothers.apps.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserPwdFindDto {

    @ApiModelProperty(value = "아이디(이메일)", required = true)
    @NotBlank(message = "아이디(이메일)")
    @Size(min = 10, max = 50, message = "아이디(이메일)")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "아이디(이메일) 형식이 맞지 않습니다.")
    private String userId;

    @ApiModelProperty(value = "이름", required = true)
    @NotBlank(message = "이름")
    @Size(min = 2, max = 20, message = "이름")
    private String userName;

    @ApiModelProperty(value = "휴대전화 번호", required = true)
    @Size(min = 10, max = 11, message = "휴대전화 번호")
    @NotBlank(message = "휴대전화 번호")
    private String userPhone;
}