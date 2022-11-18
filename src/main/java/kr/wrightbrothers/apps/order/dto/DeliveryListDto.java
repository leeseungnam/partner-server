package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.AbstractPageDto;
import kr.wrightbrothers.apps.common.type.DeliveryStatusCode;
import kr.wrightbrothers.apps.common.type.DeliveryType;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

public class DeliveryListDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class Param extends AbstractPageDto {
        private String partnerCode;         // 파트너 코드
        private String[] deliveryType;      // 배송 방법
        private String[] deliveryStatus;    // 배송 상태
        private String startDay;            // 시작 일자
        private String endDay;              // 종료 일자
        private String keywordType;         // 키워드 종류
        private String keywordValue;        // 키워드 값
        private String[] keywordValueList;  // 여러검색 조건

        // 여러 상품 검색을 위해 구분자인 ; Split 처리
        public void splitKeywordValue() {
            if (ObjectUtils.isEmpty(this.keywordValue))
                return;

            this.keywordValueList = this.keywordValue.split(";");
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String paymentDay;                  // 결제일자
        private String orderNo;                     // 주문번호
        private String orderUserName;               // 주문자
        private String deliveryStatusCode;          // 배송상태 코드
        private String deliveryStatusName;          // 배송상태 이름
        private String paymentMethodCode;           // 결제수단 코드
        private String paymentMethodName;           // 결제수단 이름
        private String orderName;                   // 주문명
        private String deliveryName;                // 배송방법 이름
        private String recipientName;               // 수령자
        private String recipientPhone;              // 휴대전화
        private String recipientAddress;            // 주소
        private String recipientAddressDetail;      // 상세주소
        private String returnFlag;                  // 반품여부

        // 주문 상태 ENUM 처리
        public void setDeliveryStatusName(String deliveryStatusName) {
            this.deliveryStatusName = DeliveryStatusCode.of(deliveryStatusName).getName();
        }
        // 결제 수단 ENUM 처리
        public void setPaymentMethodName(String paymentMethodCode) {
            this.paymentMethodName = PaymentMethodCode.of(paymentMethodCode).getName();
        }
    }

}
