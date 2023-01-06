package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReturnDeliveryDto {

    @Getter @Builder
    public static class Param {
        /** 파트너코드 */
        private String partnerCode;

        /** 주문번호 */
        private String orderNo;

        /** 주문상품 SEQ */
        private Integer orderProductSeq;

        /** 사용자 아이디 */
        private String userId;
    }

    @Getter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReqBody {
        /** 주문 번호 */
        private String orderNo;

        /** 주문 상품 SEQ */
        private Integer orderProductSeq;

        /** 택배사 */
        private String deliveryCompanyCode;

        /** 송장 번호 */
        private String invoiceNo;

        /** 수령자 이름 */
        private String recipientName;

        /** 수령자 연락처 */
        private String recipientPhone;

        /** 수령자 우편번호 */
        private String recipientAddressZipCode;

        /** 수령자 주소 */
        private String recipientAddress;

        /** 수령자 상세주소 */
        private String recipientAddressDetail;

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

    @Getter
    @AllArgsConstructor
    public static class Response {
        /** 택배사 */
        private String deliveryCompanyCode;

        /** 송장번호 */
        private String invoiceNo;

        /** 수령자 이름 */
        private String recipientName;

        /** 수령자 연락처 */
        private String recipientPhone;

        /** 수령자 우편번호 */
        private String recipientAddressZipCode;

        /** 수령자 주소 */
        private String recipientAddress;

        /** 수령자 상세주소 */
        private String recipientAddressDetail;

        /** 파트너코드 */
        private String partnerCode;

        /** 상품코드 */
        private String productCode;

        /** 상품명 */
        private String productName;
    }

}
