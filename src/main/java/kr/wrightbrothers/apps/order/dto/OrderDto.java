package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderDto {
    private String orderNo;                 // 주문 번호
    private String orderDate;               // 주문 일시
    private String orderStatusCode;         // 주문 상태 코드
    private String orderStatusName;         // 주문 상태 이름
    private Integer orderQty;               // 주문 수량
    private String orderUserCode;           // 주문자 회원 번호
    private String orderUserId;             // 주문자 아이디
    private String orderUserName;           // 주문자 이름
    private String orderUserPhone;          // 주문자 연락처
    private String recipientName;           // 수령자 이름
    private String recipientPhone;          // 수령자 연락처
    private String recipientAddressZipCode; // 수령자 우편번호
    private String recipientAddress;        // 수령자 주소
    private String recipientAddressDetail;  // 수령자 상세주소
    private String requestDetail;           // 배송 요청사항
    private String orderMemo;               // 주문 메모
    private String returnMemo;              // 반품 메모
    private String deliveryMemo;            // 배송 메모

    // 주문 상태 ENUM 처리
    public void setOrderStatusName(String orderStatusCode) {
        this.orderStatusName = OrderStatusCode.of(orderStatusCode).getName();
    }
}