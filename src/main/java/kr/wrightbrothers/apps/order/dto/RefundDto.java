package kr.wrightbrothers.apps.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefundDto {
    private String refundBankCode;      // 환불 은행 코드
    private String refundBankName;      // 환불 은행 이름
    private String refundBankAccountNo; // 환불 계좌 번호
    private String refundDepositorName; // 환불 계좌 예금주
}
