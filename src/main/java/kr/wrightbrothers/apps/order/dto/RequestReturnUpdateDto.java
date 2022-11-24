package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestReturnUpdateDto {
        private String orderNo;                 // 주문 번호
        private Integer[] orderProductSeqArray; // 주문 상품 SEQ
        private String returnProcessCode;       // 반품 요청 구분
        private String requestCode;             // 요청 처리 코드
        private String requestValue;            // 요청 처리 데이터

        private Long returnDeliveryAmount;      // 반품 배송비
        private Long paymentAmount;             // 결제 금액
        private Long refundAmount;              // 환불 예정 금액

        private String partnerCode;             // 파트너 코드
        @JsonIgnore
        private String userId;                  // 사용자 아이디
        @JsonIgnore
        private Integer orderProductSeq;        // 주문 상품 SEQ

        public void setAopPartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }

        public void setAopUserId(String userId) {
            this.userId = userId;
        }

        public void setReturnDeliveryAmount(Long returnDeliveryAmount) {
                this.returnDeliveryAmount = returnDeliveryAmount;
        }

        public void setPaymentAmount(Long paymentAmount) {
                this.paymentAmount = paymentAmount;
        }

        public void setRefundAmount(Long refundAmount) {
                this.refundAmount = refundAmount;
        }

        public void setOrderProductSeq(Integer orderProductSeq) {
            this.orderProductSeq = orderProductSeq;
        }

        public void setReturnProcessCode(String returnProcessCode){
            this.returnProcessCode = returnProcessCode;
        }

        public Object toCancelQueueDto(String statusCode) {
                return
                OrderProductStatusCode.REQUEST_COMPLETE_RETURN.getCode().equals(statusCode) ?
                        PaymentCancelDto.Queue.builder()
                                .ordNo(this.orderNo)
                                .prnrCd(this.partnerCode)
                                .stusCd(statusCode)
                                .ordPrdtIdx(Arrays.stream(this.orderProductSeqArray).map(String::valueOf).collect(Collectors.toList()))
                                .cncRsnCd(this.requestCode)
                                .cncRsn(this.requestValue)
                                .payAmt(this.paymentAmount)
                                .rtrnDlvrAmt(this.returnDeliveryAmount)
                                .refundAmt(this.refundAmount)
                                .usrId(this.userId)
                                .build()
                        :
                        DeliveryPreparingDto.Queue.builder()
                                .ordNo(this.orderNo)
                                .prnrCd(this.partnerCode)
                                .stusCd(statusCode)
                                .ordPrdtIdx(Arrays.stream(this.orderProductSeqArray).map(String::valueOf).collect(Collectors.toList()))
                                .rsnCd(this.requestCode)
                                .rsnNm(this.requestValue)
                                .usrId(this.userId)
                                .build()
                        ;
        }

        public Object toApprovalQueueDto(String statusCode) {
                return DeliveryPreparingDto.Queue.builder()
                        .ordNo(this.orderNo)
                        .prnrCd(this.partnerCode)
                        .stusCd(statusCode)
                        .ordPrdtIdx(Arrays.stream(this.orderProductSeqArray).map(String::valueOf).collect(Collectors.toList()))
                        .dlvrCmpnyCd(this.requestCode)
                        .invcNo(this.requestValue)
                        .usrId(this.userId)
                        .build();
        }

}

