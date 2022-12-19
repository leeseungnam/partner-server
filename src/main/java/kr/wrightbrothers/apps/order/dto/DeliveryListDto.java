package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.AbstractPageDto;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import kr.wrightbrothers.apps.common.constants.PaymentConst;
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
        /** 파트너 코드 */
        private String partnerCode;

        /** 배송 방법 */
        private String[] deliveryType;

        /** 배송 상태 */
        private String[] deliveryStatus;

        /** 시작 일자 */
        private String startDay;

        /** 종료 일자 */
        private String endDay;

        /** 키워드 종류 */
        private String keywordType;

        /** 키워드 값 */
        private String keywordValue;

        /** 다중검색 조건 */
        private String[] keywordValueList;

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
        /** 결제일자 */
        private String paymentDay;

        /** 주문번호 */
        private String orderNo;

        /** 주문자 */
        private String orderUserName;

        /** 배송상태 코드 */
        private String deliveryStatusCode;

        /** 배송상태 이름 */
        private String deliveryStatusName;

        /** 결제수단 코드 */
        private String paymentMethodCode;

        /** 결제수단 이름 */
        private String paymentMethodName;

        /** 주문명 */
        private String orderName;

        /** 배송방법 이름 */
        private String deliveryName;

        /** 수령자 */
        private String recipientName;

        /** 휴대전화 */
        private String recipientPhone;

        /** 주소 */
        private String recipientAddress;

        /** 상세주소 */
        private String recipientAddressDetail;

        /** 반품여부 */
        private String returnFlag;

        /** 배송비 결제방법 */
       private String deliveryPaymentType;

        // 주문 상태 ENUM 처리
        public void setDeliveryStatusName(String deliveryStatusName) {
            this.deliveryStatusName = OrderConst.Status.of(deliveryStatusName).getName();
        }
        // 결제 수단 ENUM 처리
        public void setPaymentMethodName(String paymentMethodCode) {
            this.paymentMethodName = PaymentConst.Method.of(paymentMethodCode).getName();
        }
    }

}
