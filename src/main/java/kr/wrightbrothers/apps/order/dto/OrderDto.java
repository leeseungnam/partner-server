package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.constants.OrderConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class OrderDto {
    /** 주문 번호 */
    private String orderNo;

    /** 주문 일시 */
    private String orderDate;

    /** 주문 상태 코드 */
    private String orderStatusCode;

    /** 주문 상태 이름 */
    private String orderStatusName;

    /** 주문 수량 */
    private Integer orderQty;

    /** 주문자 회원 번호 */
    private String orderUserCode;

    /** 주문자 아이디 */
    private String orderUserId;

    /** 주문자 이름 */
    private String orderUserName;

    /** 주문자 연락처 */
    private String orderUserPhone;

    /** 수령자 이름 */
    private String recipientName;

    /** 수령자 연락처 */
    private String recipientPhone;

    /** 수령자 우편번호 */
    private String recipientAddressZipCode;

    /** 수령자 주소 */
    private String recipientAddress;

    /** 수령자 상세주소 */
    private String recipientAddressDetail;

    /** 요청사항 */
    private String requestDetail;

    /** 주문 메모 */
    private String orderMemo;

    /** 반품 메모 */
    private String returnMemo;

    /** 배송 메모 */
    private String deliveryMemo;

    // 주문 상태 ENUM 처리
    public void setOrderStatusName(String orderStatusCode) {
        this.orderStatusName = OrderConst.Status.of(orderStatusCode).getName();
    }
}