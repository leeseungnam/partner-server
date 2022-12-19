package kr.wrightbrothers.apps.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class OrderFindDto {

    @Getter @Builder
    @AllArgsConstructor
    public static class Param {
        /** 파트너 코드 */
        private String partnerCode;

        /** 주문 번호 */
        private String orderNo;
    }

    @Getter @Builder
    public static class Response {
        /** 주문 정보 */
        private OrderDto order;

        /** 결제 정보 */
        private PaymentDto payment;

        /** 주문 상품 목록 */
        private List<ProductDto> productList;
    }

}
