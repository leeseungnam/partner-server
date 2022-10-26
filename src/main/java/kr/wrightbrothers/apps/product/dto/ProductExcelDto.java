package kr.wrightbrothers.apps.product.dto;

import kr.wrightbrothers.apps.common.type.DeliveryType;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductExcelDto {
    private int optionCount;                // 옵션수량
    private String productCode;             // 상품코드
    private String brandName;               // 브랜드
    private String categoryOneName;         // 대분류
    private String categoryTwoName;         // 중분류
    private String categoryThrName;         // 소분류
    private String productName;             // 상품명
    private String productOption;           // 상품옵션
    private int productStockQty;            // 재고
    private Long finalSellAmount;           // 최종 판매가
    private String productStatusCode;       // 상품상태
    private String displayFlag;             // 전시상태
    private String deliveryType;            // 배송정보
    private String deliveryBundleFlag;      // 묶음배송 여부
    private String productSellStartDay;     // 판매 시작일
    private String productSellEndDay;       // 판매 완료일
    private String createDay;               // 등록일자
    private String updateDay;               // 수정일자
    private String createUserName;          // 등록자

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = DeliveryType.of(deliveryType).getName();
    }

    public void setProductStatusCode(String productStatusCode) {
        this.productStatusCode = ProductStatusCode.of(productStatusCode).getName();
    }
}
