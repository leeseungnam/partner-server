package kr.wrightbrothers.apps.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDto {
    private Integer orderProductSeq;    // 주문 상품 인덱스
    private String productCode;         // 상품 코드
    private String productName;         // 상품 이름
    private Long finalSellAmount;       // 상품 금액
    private String optionName;          // 옵션 이름
    private Long optionSurcharge;       // 변동 금액
    private Integer productQty;         // 구매 수량
}
