package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReturnProductDto {
    private Integer orderProductSeq;            // 주문 상품 인덱스
    private String productCode;                 // 상품 코드
    private String productName;                 // 상품 이름
    private String optionName;                  // 옵션 이름
    private Long finalSellAmount;               // 판매 금액
    private String returnRequestDay;            // 반품 요청 일자
    private String returnCompleteDay;           // 반품 완료 일자
    private Integer productQty;                 // 반품 요청 수량
    private String orderProductStatusCode;      // 상품 진행 상태 코드(반품 진행 상태 코드)
    private String orderProductStatusName;      // 상품 진행 상태 이름(반품 진행 상태 이름)
    private String returnDeliveryCompanyCode;   // 반품 택배사 코드
    private String returnDeliveryCompanyName;   // 반품 택배사 이름
    private String returnInvoiceNo;             // 반품 송장번호
    private String returnReason;                // 반품 사유
    private String nonReturnReason;             // 반품불가 사유

    // 주문 상품 상태 ENUM 처리
    public void setOrderProductStatusName(String orderProductStatusCode) {
        this.orderProductStatusName = OrderProductStatusCode.of(orderProductStatusCode).getName();
    }
}
