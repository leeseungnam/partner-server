package kr.wrightbrothers.apps.brand.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModelListDto {
    @ApiModelProperty(value = "모델 코드")
    private String modelCode;       // 모델코드
    @ApiModelProperty(value = "모델 이름")
    private String modelName;       // 모델이름
}
