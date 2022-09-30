package kr.wrightbrothers.apps.sign.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel(value = "로그인 요청 데이터")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignDto {

    private String userId;
    private String userPwd;

}
