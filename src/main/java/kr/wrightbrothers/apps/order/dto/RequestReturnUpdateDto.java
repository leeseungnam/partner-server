package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import lombok.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestReturnUpdateDto {
        /** 주문 번호 */
        private String orderNo;

        /** 주문 상품 SEQ Array */
        private Integer[] orderProductSeqArray;

        /** 반품 요청 구분 */
        private String returnProcessCode;

        /** 요청 처리 코드 */
        private String requestCode;

        /** 요청 처리 데이터 */
        private String requestValue;

        /** 반품 배송비 */
        private Long returnDeliveryAmount;

        /** 결제 금액 */
        private Long paymentAmount;

        /** 환불 예정 금액 */
        private Long refundAmount;

        /** 파트너 코드 */
        private String partnerCode;

        /** 사용자 아이디 */
        @JsonIgnore
        private String userId;

        /** 주문 상품 SEQ */
        @JsonIgnore
        private Integer orderProductSeq;

        /** 배송비 */
        @JsonIgnore
        private Long deliveryAmount;

        public void setAopPartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }

        public void setAopUserId(String userId) {
            this.userId = userId;
        }

        public Object toCancelQueueDto(String statusCode) {
                return
                        OrderConst.ProductStatus.REQUEST_COMPLETE_RETURN.getCode().equals(statusCode) ?
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

