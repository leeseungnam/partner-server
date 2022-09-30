package kr.wrightbrothers.apps.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel(value = "회원권한 데이터")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthDto {

    @ApiModelProperty(value = "권한 코드", required = true)
    private String authCode;
    @ApiModelProperty(value = "파트너 코드", required = true)
    private String partnerCode;
    @ApiModelProperty(value = "파트너 타입", required = true)
    private String partnerKind;

}
