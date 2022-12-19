package kr.wrightbrothers.apps.brand.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BrandListDto {
    /** 브랜드 번호 */
    private String brandNo;

    /** 브랜드 이름 */
    private String brandName;
}
