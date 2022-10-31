package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
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
        private String requestName;             // 요청 처리 이름
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

        public PaymentCancelDto.Queue toCancelQueueDto(PaymentCancelDto.BankInfo bankInfo) {
                // Null Safe
                bankInfo = Optional.ofNullable(bankInfo).orElseGet(PaymentCancelDto.BankInfo::new);

                return PaymentCancelDto.Queue.builder()
                        .ordNo(this.orderNo)
                        .prnrCd(this.partnerCode)
                        .ordPrdtIdxList(Arrays.stream(this.orderProductSeqArray).map(orderProductSeq -> PaymentCancelDto.Queue_Int.builder().ordPrdtIdx(orderProductSeq).build()).collect(Collectors.toList()))
                        .bankCd(bankInfo.getBankCd())
                        .bankAcntNo(bankInfo.getBankAcntNo())
                        .dpstrNm(bankInfo.getDpstrNm())
                        .build();
        }

}

