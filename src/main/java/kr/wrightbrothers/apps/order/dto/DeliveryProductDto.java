package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.constants.DeliveryConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryProductDto {
    /** 주문 상품 SEQ */
    private Integer orderProductSeq;

    /** 상품 코드 */
    private String productCode;

    /** 상품 이름 */
    private String productName;

    /** 옵션 명 */
    private String optionName;

    /** 상품 수량 */
    private Integer productQty;

    /** 배송 방법 타입 */
    private String deliveryType;

    /** 배송 방법 명 */
    private String deliveryName;

    /** 택배사 코드 */
    private String deliveryCompanyCode;

    /** 택배사 명 */
    private String deliveryCompanyName;

    /** 송장번호 */
    private String invoiceNo;

    /** 수령자 명 */
    private String recipientName;

    /** 수령자 연락처 */
    private String recipientPhone;

    /** 수령자 주소 */
    private String recipientAddress;

    /** 배송 시작일 */
    private String deliveryStartDay;

    /** 배송 완료일 */
    private String deliveryEndDay;

    /** 배송 상태 코드 */
    private String deliveryStatusCode;

    /** 배송 상태 명 */
    private String deliveryStatusName;

    public void setDeliveryStatusName(String deliveryStatusCode) {
        this.deliveryStatusName = DeliveryConst.Status.of(deliveryStatusCode).getName();
    }
}
