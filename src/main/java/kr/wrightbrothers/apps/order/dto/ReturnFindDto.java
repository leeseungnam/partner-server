package kr.wrightbrothers.apps.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ReturnFindDto {

    @Getter
    @Builder
    public static class Param {
        private String partnerCode;                 // 파트너 코드
        private String orderNo;                     // 주문 번호
    }

    @Getter
    @Builder
    public static class Response {
        private OrderDto order;                     // 주문 정보
        private PaymentDto payment;                 // 결제 정보
        List<ProductDto> productList;               // 주문 상품 목록
        List<ReturnProductDto> returnProductList;   // 반품 요청 상품 리스트
    }
}
