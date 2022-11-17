package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class DeliveryAddressDto {

    @Getter
    @Builder
    public static class Param {
        private String orderNo;                 // 주문번호
        private Integer orderProductSeq;        // 주문상품 SEQ

        private String partnerCode;             // 파트너 코드
        @JsonIgnore
        private String userId;                  // 사용자 아이디

        public void setAopPartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }
        public void setAopUserId(String userId) {
            this.userId = userId;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String recipientName;           // 수령자 이름
        private String recipientPhone;          // 수령자 휴대전화
        private String recipientAddressZipCode; // 수령자 우편번호
        private String recipientAddress;        // 수령자 주소
        private String recipientAddressDetail;  // 수령자 상세주소
    }

}
