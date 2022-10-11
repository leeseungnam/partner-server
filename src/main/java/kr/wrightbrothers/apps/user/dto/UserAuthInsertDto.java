package kr.wrightbrothers.apps.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class UserAuthInsertDto {

    @ApiModel(value = "회원권한 데이터")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody extends UserAuthDto {
        @ApiModelProperty(value = "유저 아이디")
        private String userId;

        public void changeUserId(String userId) {
            this.userId = userId;
        }
    }
}
