package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.AbstractPageDto;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.type.PaymentStatusCode;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

public class OrderListDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class Param extends AbstractPageDto {
        private String partnerCode;         // 파트너 코드
        private String[] orderStatus;       // 전시 상태
        private String[] paymentStatus;     // 결제 상태
        private String[] paymentMethod;     // 결제 방법
        private String rangeType;           // 조회 종류
        private String startDay;            // 시작 일자
        private String endDay;              // 종료 일자
        private String keywordType;         // 키워드 종류
        private String keywordValue;        // 키워드 값
        private String sortType;            // 정렬 타입
        private String[] keywordValueList;  // 여러검색 조건

        // 파라미터 초기 설정
        public void parameterInit() {
            // 반품상태 체크시 일괄 상태 추가
            if (Arrays.toString(orderStatus).contains("O10")) {
                this.orderStatus = ArrayUtils.addAll(this.orderStatus, "R01", "R02", "R03", "R04", "R05", "R06");
            }

            // 여러 상품 검색을 위해 구분자인 ; Split 처리
            if (ObjectUtils.isEmpty(this.keywordValue))
                return;

            this.keywordValueList = this.keywordValue.split(";");

        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String orderNo;             // 주문번호
        private String orderDay;            // 주문일자
        private String orderUserName;       // 주문자명
        private String orderStatusCode;     // 주문상태 코드
        private String orderStatusName;     // 주문상태 명
        private String paymentMethodCode;   // 결제방법 코드
        private String paymentMethodName;   // 결제방법 명
        private String orderName;           // 주문명
        private Long orderAmount;           // 주문금액
        private Long paymentAmount;         // 결제금액
        private String paymentDay;          // 결제일자
        private String cancelDay;           // 취소일자
        private String paymentStatusCode;   // 결제상태 코드
        private String paymentStatusName;   // 결제상테 명

        // 주문 상태 ENUM 처리
        public void setOrderStatusName(String orderStatusCode) {
            this.orderStatusName = OrderStatusCode.of(orderStatusCode).getName();
        }
        // 결제 상태 ENUM 처리
        public void setPaymentStatusName(String paymentStatusCode) {
            this.paymentStatusName = PaymentStatusCode.of(paymentStatusCode).getName();
        }
        // 결제 수단 ENUM 처리
        public void setPaymentMethodName(String paymentMethodCode) {
            this.paymentMethodName = PaymentMethodCode.of(paymentMethodCode).getName();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Statistics {
        private long totalOrderCount;       // 전체주문 건수
        private long completeOrderCount;    // 주문완료 건수
        private long readyProductCount;     // 상품준비 건수
        private long requestCancelCount;    // 취소요청 건수
        private long confirmPurchaseCount;  // 구매확정 건수
        private long completeReturnCount;   // 반품완료 건수
    }

}
