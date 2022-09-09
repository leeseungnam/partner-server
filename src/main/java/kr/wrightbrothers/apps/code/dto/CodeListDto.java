package kr.wrightbrothers.apps.code.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CodeListDto {
    @ApiModelProperty(value = "코드 값")
    private String codeValue;       // 코드 값
    @ApiModelProperty(value = "코드 이름")
    private String codeName;        // 코드 이름
}
