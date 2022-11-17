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
        private String partnerCode;  // 파트너 코드
        private String orderNo;      // 주문 번호
    }

    @Getter
    @Builder
    public static class Response {
        private OrderDto order;                             // 주문 정보
        private PaymentDto payment;                         // 결제 정보
        private List<DeliveryProductDto> deliveryList;      // 배송 상품 목록
    }

}
