package kr.wrightbrothers.apps.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderAuthDto {
    private String partnerCode;     // 파트너 코드
    private String orderNo;         // 주문번호
}
