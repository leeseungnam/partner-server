package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PaymentRefundDto {

    @Getter
    @Builder
    public static class Param {
        private String partnerCode;         // 파트너 코드
        private String orderNo;             // 주문 번호
        private Integer orderProductSeq;     // 주문 상품 번호
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reason {
        @NotBlank(message = "주문번호")
        private String orderNo;             // 주문 번호
        @NotNull(message = "주문상품 SEQ")
        private Integer orderProductSeq;    // 주문 상품 SEQ
        @NotBlank(message = "환불 은행")
        private String refundBankCode;      // 환불 은행 코드
        @NotBlank(message = "환불 계좌 번호")
        private String refundBankAccountNo; // 환불 계좌 번호
        @NotBlank(message = "예금주")
        private String refundDepositorName; // 환불 계좌 예금주
    }

    @Getter
    @AllArgsConstructor
    public static class ResBody extends Reason {}

    @Getter
    @Jacksonized
    @SuperBuilder
    public static class ReqBody extends Reason {
        private String partnerCode;
        @JsonIgnore
        private String userId;

        public void setAopPartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }
        public void setAopUserId(String userId) {
            this.userId = userId;
        }
    }

}
