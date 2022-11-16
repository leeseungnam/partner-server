package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.type.DeliveryType;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDto {
    private Integer orderProductSeq;            // 주문 상품 인덱스
    private String productCode;                 // 상품 코드
    private String productName;                 // 상품 이름
    private String orderProductStatusCode;      // 주문 상품 상태 코드
    private String orderProductStatusName;      // 주문 상품 상태 명
    private Long finalSellAmount;               // 상품 금액
    private String optionName;                  // 옵션 이름
    private Long optionSurcharge;               // 변동 금액
    private Integer productQty;                 // 구매 수량
    private String deliveryType;                // 배송 구분 타입
    private String deliveryName;                // 배송 구분 명
    private Long deliveryChargeAmount;          // 배송료
    private String cancelDay;                   // 취소 일시
    private String cancelReason;                // 취소 사유
    private String returnDeliveryEndDay;        // 반품 완료 일자
    private String returnInvoiceNo;             // 반품 배송 번호

    public void setOrderProductStatusName(String productStatusCode) {
        this.orderProductStatusName = OrderProductStatusCode.of(productStatusCode).getName();
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = DeliveryType.of(deliveryName).getName();
    }
}
