package kr.wrightbrothers.apps.brand.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModelListDto {
    /** 모델 코드 */
    private String modelCode;

    /** 모델 이름 */
    private String modelName;
}
