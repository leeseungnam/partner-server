package kr.wrightbrothers.apps.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class DeliveryFindDto {

    @Getter
    @Builder
    public static class StatusCheck {
        private String orderNo;
        private Integer orderProductSeq;
    }

    @Getter
    @Builder
    public static class Param {
        private String partnerCode;
        private String orderNo;
    }

    @Getter
    @Builder
    public static class Response {
        private OrderDto order;                 // 주문 정보
        private PaymentDto payment;             // 결제 정보
        private List<ProductDto> productList;   // 주문 상품 목록
    }

}
