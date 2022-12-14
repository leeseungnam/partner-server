package kr.wrightbrothers.apps.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductParamDto {
    /** 파트너 코드 */
    private String partnerCode;

    /** 상품 코드 */
    private String productCode;

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
}
