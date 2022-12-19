package kr.wrightbrothers.apps.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class ProductAuthDto {
    /** 파트너 코드 */
    private String partnerCode;     // 파트너 코드

    /** 상품 코드 */
    private String productCode;     // 상품 코드
}
