package kr.wrightbrothers.apps.brand.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModelListDto {
    private String modelCode;       // 모델코드
    private String modelName;       // 모델이름
}
