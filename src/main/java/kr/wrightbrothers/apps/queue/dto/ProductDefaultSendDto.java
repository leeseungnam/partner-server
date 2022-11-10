package kr.wrightbrothers.apps.queue.dto;

import lombok.Getter;

@Getter
public class ProductDefaultSendDto {
    private final String partnerCode;     // 파트너 코드
    private final String productCode;     // 상품 코드

    public ProductDefaultSendDto(String partnerCode, String productCode) {
        this.partnerCode = partnerCode;
        this.productCode = productCode;
    }
}
