package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.constants.DeliveryConst;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDto {
    /** 주문 상품 SEQ */
    private Integer orderProductSeq;

    /** 상품 코드 */
    private String productCode;

    /** 상품 이름 */
    private String productName;

    /** 주문 상품 상태 코드 */
    private String orderProductStatusCode;

    /** 주문 싱픔 싱테 이름 */
    private String orderProductStatusName;

    /** 상품 금액 */
    private Long finalSellAmount;

    /** 옵션 이름 */
    private String optionName;

    /** 변동 금액 */
    private Long optionSurcharge;

    /** 구매 수량 */
    private Integer productQty;

    /** 배송 구분 타입 */
    private String deliveryType;

    /** 배송 구분 명 */
    private String deliveryName;

    /** 배송료 */
    private Long deliveryChargeAmount;

    /** 취소 일자 */
    private String cancelDay;

    /** 취소사유 */
    private String cancelReason;

    /** 반품완료 일자 */
    private String returnDeliveryEndDay;

    /** 반품 택배사 */
    private String returnDeliveryCompany;

    /** 반품 배송 번호 */
    private String returnInvoiceNo;

    /** 포인트 */
    private Long sspPoint;

    /** 판매대금 */
    private Long saleAmount;

    public void setOrderProductStatusName(String productStatusCode) {
        this.orderProductStatusName = OrderConst.ProductStatus.of(productStatusCode).getName();
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = DeliveryConst.Type.of(deliveryName).getName();
    }
}
