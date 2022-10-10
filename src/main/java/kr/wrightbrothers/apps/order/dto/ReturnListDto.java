package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.AbstractPageDto;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.type.PaymentStatusCode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

public class ReturnListDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class Param extends AbstractPageDto {
        private String partnerCode;
        private String[] returnStatus;
        private String rangeType;
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
        private String returnRequestDay;            // 반품 요청 일자
        private String orderNo;                     // 주문번호
        private String orderDay;                    // 주문일자
        private String orderUserName;               // 주문자
        private String orderProductStatusCode;      // 반품상태 코드
        private String orderProductStatusName;      // 반품상태 이름
        private String orderStatusCode;             // 주문상태 코드
        private String orderStatusName;             // 주문상태 이름
        private String paymentMethodCode;           // 결제수단 코드
        private String paymentMethodName;           // 결제수단 이름
        private String orderName;                   // 주문명
        private String productName;                 // 반품요청 상품
        private String returnReason;                // 반품사유
        private Long orderAmount;                   // 주문금액
        private Long finalSellAmount;               // 판매금액
        private Long sspPoint;                      // S.S.P
        private Long salesAmount;                   // 판매대금
        private String cancelDay;                   // 취소일자
        private String paymentStatusCode;           // 결제상태 코드
        private String paymentStatusName;           // 결제상태 이름

        // 반품 상태 ENUM 처리
        public void setOrderProductStatusName(String orderProductStatusCode) {
            this.orderProductStatusName = OrderProductStatusCode.of(orderProductStatusCode).getName();
        }

        // 주문 상태 ENUM 처리
        public void setOrderStatusName(String orderStatusCode) {
            this.orderStatusName = OrderStatusCode.of(orderStatusCode).getName();
        }

        // 결제 수단 ENUM 처리
        public void setPaymentMethodName(String paymentMethodCode) {
            this.paymentMethodName = PaymentMethodCode.of(paymentMethodCode).getName();
        }

        // 결제 상태 ENUM 처리
        public void setPaymentStatusName(String paymentStatusCode) {
            this.paymentStatusName = PaymentStatusCode.of(paymentStatusCode).getName();
        }
    }

}
