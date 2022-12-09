package kr.wrightbrothers.apps.product.dto;

import kr.wrightbrothers.apps.common.annotation.ExcelBody;
import kr.wrightbrothers.apps.common.type.DeliveryType;
import kr.wrightbrothers.apps.common.type.ExcelBodyType;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductExcelDto {
    /** 옵션 수량 */
    private int optionCount;

    /** 상품 코드 */
    @ExcelBody(colIndex = 1)
    private String productCode;

    /** 브랜드 */
    @ExcelBody(colIndex = 2)
    private String brandName;

    /** 대분류 */
    @ExcelBody(colIndex = 3)
    private String categoryOneName;

    /** 중분류 */
    @ExcelBody(colIndex = 4)
    private String categoryTwoName;

    /** 소분류 */
    @ExcelBody(colIndex = 5)
    private String categoryThrName;

    /** 싱픔먕 */
    @ExcelBody(colIndex = 6, bodyType = ExcelBodyType.LONG_TEXT)
    private String productName;

    /** 상품옵션 */
    @ExcelBody(colIndex = 7, bodyType = ExcelBodyType.LONG_TEXT)
    private String productOption;

    /** 재고 */
    @ExcelBody(colIndex = 8, bodyType = ExcelBodyType.NUMBER)
    private int productStockQty;

    /** 최종 판매가 */
    @ExcelBody(colIndex = 9, bodyType = ExcelBodyType.NUMBER)
    private Long finalSellAmount;

    /** 상품상태 */
    @ExcelBody(colIndex = 10)
    private String productStatusCode;

    /** 전시상태 */
    @ExcelBody(colIndex = 11)
    private String displayFlag;

    /** 배송정보 */
    @ExcelBody(colIndex = 12)
    private String deliveryType;

    /** 묶음배송 여부 */
    @ExcelBody(colIndex = 13)
    private String deliveryBundleFlag;

    /** 판매 시작일 */
    @ExcelBody(colIndex = 14)
    private String productSellStartDay;

    /** 판매 완료일 */
    @ExcelBody(colIndex = 15)
    private String productSellEndDay;

    /** 등록일자 */
    @ExcelBody(colIndex = 16)
    private String createDay;

    /** 수정일자 */
    @ExcelBody(colIndex = 17)
    private String updateDay;

    /** 등록자 */
    @ExcelBody(colIndex = 18, bodyType = ExcelBodyType.LONG_TEXT)
    private String createUserName;

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = DeliveryType.of(deliveryType).getName();
    }

    public void setProductStatusCode(String productStatusCode) {
        this.productStatusCode = ProductStatusCode.of(productStatusCode).getName();
    }
}
