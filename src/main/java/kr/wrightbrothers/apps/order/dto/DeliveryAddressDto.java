package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class DeliveryAddressDto {

    @Getter @Builder
    public static class Param {
        /** 주문번호 */
        private String orderNo;

        /** 주문상품 SEQ */
        private Integer orderProductSeq;

        /** 파트너 코드 */
        private String partnerCode;

        /** 사용자 아이디 */
        @JsonIgnore
        private String userId;

        public void setAopPartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }
        public void setAopUserId(String userId) {
            this.userId = userId;
        }
    }

    @Getter @Builder
    @AllArgsConstructor
    public static class Response {
        /** 이름 */
        private String recipientName;

        /** 휴대전화 */
        private String recipientPhone;

        /** 우편번호 */
        private String recipientAddressZipCode;

        /** 주소 */
        private String recipientAddress;

        /** 상세주소 */
        private String recipientAddressDetail;

        /** 송장번호 */
        private String invoiceNo;
    }

}
