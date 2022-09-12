package kr.wrightbrothers.apps.category.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryListDto {
    @ApiModelProperty(value = "카테고리 코드")
    private String categoryCode;    // 카테고리 코드
    @ApiModelProperty(value = "카테고리 이름")
    private String categoryName;    // 카테고리 이름
}
