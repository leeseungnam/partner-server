package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestReturnUpdateDto {
        @NotBlank(message = "주문 번호")
        private String orderNo;                 // 주문 번호

        @NotNull(message = "주문 상품 SEQ")
        private Integer[] orderProductSeqArray; // 주문 상품 SEQ

        @NotBlank(message = "반품 요청 처리 구분")
        private String returnProcessCode;       // 반품 요청 구분

        private String requestCode;             // 요청 처리 코드
        private String requestValue;            // 요청 처리 데이터

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

