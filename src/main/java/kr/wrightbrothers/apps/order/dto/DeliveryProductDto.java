package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.type.DeliveryStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryProductDto {
    private Integer orderProductSeq;        // 주문 상품 SEQ
    private String productCode;             // 상품 코드
    private String productName;             // 상품 이름
    private String optionName;              // 옵션 이름
    private Integer productQty;             // 상품 수량
    private String deliveryCompanyCode;     // 택배사 코드
    private String deliveryCompanyName;     // 택배사 이름
    private String invoiceNo;               // 송장번호
    private String recipientName;           // 수령자 명
    private String recipientPhone;          // 수령자 연락처
    private String recipientAddressZipCode; // 수령자 우편번호
    private String recipientAddress;        // 수령자 주소
    private String recipientAddressDetail;  // 수령자 상세주소
    private String deliveryStatusCode;      // 택배 진행 상태 코드
    private String deliveryStatusName;      // 택배 진행 상태 이름
    private String deliveryStartDay;        // 배송 시작일
    private String deliveryEndDay;          // 배송 완료일

    public void setDeliveryStatusName(String deliveryStatusCode) {
        this.deliveryStatusName = DeliveryStatusCode.of(deliveryStatusCode).getName();
    }
}
