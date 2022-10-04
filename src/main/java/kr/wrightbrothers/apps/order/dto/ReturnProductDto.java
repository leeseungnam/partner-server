package kr.wrightbrothers.apps.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReturnProductDto {
    private Integer orderProductSeq;        // 주문 상품 인덱스
    private String orderProductStatusName;  // 상품 진행 상태
    private String productCode;             // 상품 코드
    private String productName;             // 상품 이름
    private String optionName;              // 옵션 이름
    private Long finalSellAmount;           // 결제 금액
    private String returnRequestDay;        // 반품 요청일
    private String nonReturnReason;         // 반품 불가 사유
}
