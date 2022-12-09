package kr.wrightbrothers.apps.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class OrderAuthDto {
    /** 파트너 코드 */
    private String partnerCode;

    /** 주문번호 */
    private String orderNo;
}
