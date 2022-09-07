package kr.wrightbrothers.apps.brand.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BrandListDto {
    @ApiModelProperty(value = "브랜드 번호")
    private String brandNo;     // 브랜드 번호
    @ApiModelProperty(value = "브랜드 이름")
    private String brandName;   // 브랜드 이름
}
