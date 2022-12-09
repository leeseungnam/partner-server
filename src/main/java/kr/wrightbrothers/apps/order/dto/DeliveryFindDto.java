package kr.wrightbrothers.apps.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class DeliveryFindDto {

    @Getter @Builder
    public static class StatusCheck {
        /** 주문번호 */
        private String orderNo;

        /** 주문상품 SEQ */
        private Integer orderProductSeq;
    }

    @Getter @Builder
    public static class Param {
        /** 파트너 코드 */
        private String partnerCode;

        /** 주문번호 */
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

        /** 배송상품 목록 */
        private List<DeliveryProductDto> deliveryList;
    }

}
