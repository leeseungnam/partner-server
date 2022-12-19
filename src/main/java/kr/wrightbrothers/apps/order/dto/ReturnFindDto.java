package kr.wrightbrothers.apps.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ReturnFindDto {

    @Getter @Builder
    public static class Param {
        /** 파트너 코드 */
        private String partnerCode;

        /** 주문 번호 */
        private String orderNo;

        public OrderFindDto.Param toOrderFindParam() {
            return new OrderFindDto.Param(this.partnerCode, this.orderNo);
        }
    }

    @Getter @Builder
    public static class Response {
        /** 주문 */
        private OrderDto order;

        /** 결제 */
        private PaymentDto payment;

        /** 반품요청 상품 */
        List<ReturnProductDto> returnProductList;
    }
}
