package kr.wrightbrothers.apps.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryListDto {
    private String categoryCode;    // 카테고리 코드
    private String categoryName;    // 카테고리 이름
}
