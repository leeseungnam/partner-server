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

public class ReturnListDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class Param extends AbstractPageDto {
        /** 파트너 코드 */
        private String partnerCode;

        /** 반품 상태 */
        private String[] returnStatus;

        /** 검색기간 구분 */
        private String rangeType;

        /** 시작일자 */
        private String startDay;

        /** 종료일자 */
        private String endDay;

        /** 키워드 종료 */
        private String keywordType;

        /** 키워드 값 */
        private String keywordValue;

        /** 다중검색 */
        private String[] keywordValueList;

        // 여러 상품 검색을 위해 구분자인 ; Split 처리
        public void splitKeywordValue() {
            if (ObjectUtils.isEmpty(this.keywordValue))
                return;

            this.keywordValueList = this.keywordValue.split(",");
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        /** 반품 요청 일자 */
        private String returnRequestDay;

        /** 주문 번호 */
        private String orderNo;

        /** 주문 일자 */
        private String orderDay;

        /** 주문자 */
        private String orderUserName;

        /** 주문 상태 코드 */
        private String returnStatusCode;

        /** 주문 상태 이름 */
        private String returnStatusName;

        /** 결제 수단 코드 */
        private String paymentMethodCode;

        /** 결제 수단 이름 */
        private String paymentMethodName;

        /** 주문명 */
        private String orderName;

        /** 반품요청 상품 */
        private String productName;

        /** 반품사유 */
        private String returnReason;

        /** 주문금액 */
        private Long orderAmount;

        /** 판매금액 */
        private Long finalSellAmount;

        /** 반품배송비 */
        private Long returnDeliveryChargeAmount;

        // 반품 상태 ENUM 처리
        public void setReturnStatusName(String returnStatusName) {
            this.returnStatusName = OrderConst.Status.of(returnStatusName).getName();
        }

        // 결제 수단 ENUM 처리
        public void setPaymentMethodName(String paymentMethodCode) {
            this.paymentMethodName = PaymentConst.Method.of(paymentMethodCode).getName();
        }

    }

}
