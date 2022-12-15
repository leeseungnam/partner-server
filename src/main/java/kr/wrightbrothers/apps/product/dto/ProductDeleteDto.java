package kr.wrightbrothers.apps.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDeleteDto {
    /** 파트너 코드 */
    private String partnerCode;

    /** 상품 코드 */
    private String[] productCodeList;

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public ProductDeleteDto(String partnerCode, String[] productCodeList) {
        this.partnerCode = partnerCode;
        this.productCodeList = productCodeList;
    }
}
