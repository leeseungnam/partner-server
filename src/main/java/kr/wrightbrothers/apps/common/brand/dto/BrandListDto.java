package kr.wrightbrothers.apps.common.brand.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BrandListDto {
    private String brandNo;     // 브랜드 번호
    private String brandName;   // 브랜드 이름
}
