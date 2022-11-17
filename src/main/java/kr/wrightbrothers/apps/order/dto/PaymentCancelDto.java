package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
@ApiModel(value = "결제취소 요청")
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelDto {
    @ApiModelProperty(value = "주문 번호", required = true)
    @NotBlank(message = "주문 번호")
    private String orderNo;                 // 주문 번호

    @ApiModelProperty(value = "주문 상품 SEQ", required = true)
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeq;      // 주문 상품 SEQ

    @ApiModelProperty(value = "취소사유 코드", required = true)
    @NotBlank(message = "취소사유 코드")
    private String cancelReasonCode;        // 취소사유 코드

    @ApiModelProperty(value = "취소사유 명", required = true)
    @NotBlank(message = "취소사유 명")
    private String cancelReasonName;        // 취소사유 명

    @ApiModelProperty(value = "결제방법", required = true)
    @NotBlank(message = "결제방법")
    private String paymentMethodCode;       // 결제방법

    @ApiModelProperty(value = "은행코드")
    private String refundBankCode;          // 은행코드
    @ApiModelProperty(value = "은행명")
    private String refundBankName;          // 은행명

    @ApiModelProperty(value = "계좌번호")
    @Size(min = 5, max = 30, message = "계좌번호")
    private String refundBankAccountNo;     // 계좌번호
    @ApiModelProperty(value = "예금주")
    private String refundDepositorName;     // 예금주

    private String partnerCode;             // 파트너 코드
    @JsonIgnore
    private String userId;                  // 사용자 아이디

    public void validRefundInfo() {
        // 무통장 결제 시 해당 유효성 체크
        if (!PaymentMethodCode.NON_BANK.getCode().equals(this.paymentMethodCode))
            return;

        if (ObjectUtils.isEmpty(this.refundBankCode))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"환불 은행 코드"});
        if (ObjectUtils.isEmpty(this.refundBankName))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"환불 은행 이름"});
        if (ObjectUtils.isEmpty(this.refundBankAccountNo))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"환불 계좌 번호"});
        if (ObjectUtils.isEmpty(this.refundDepositorName))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"환불 예금주"});
    }

    public Queue toCancelQueueDto(BankInfo bankInfo) {
        // Null Safe
        bankInfo = Optional.ofNullable(bankInfo).orElseGet(PaymentCancelDto.BankInfo::new);

        return Queue.builder()
                .ordNo(this.orderNo)
                .prnrCd(this.partnerCode)
                .ordPrdtIdx(Arrays.stream(this.orderProductSeq).map(String::valueOf).collect(Collectors.toList()))
                .cncRsn(this.cancelReasonCode)
                .bankCd(bankInfo.getBankCd())
                .bankAcntNo(bankInfo.getBankAcntNo())
                .dpstrNm(bankInfo.getDpstrNm())
                .usrId(this.userId)
                .build();
    }

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
    public void setAopUserId(String userId) {
        this.userId = userId;
    }

    @Getter
    @Builder
    public static class Queue {
        private String ordNo;                   // 주문번호
        private String prnrCd;                  // 파트너코드
        private String stusCd;                  // 상태코드
        private List<String> ordPrdtIdx;        // 주문상품 IDX 배열
        private String cncRsn;                  // 취소 사유 코드
        private String bankCd;                  // 은행코드
        private String bankAcntNo;              // 계좌번호
        private String dpstrNm;                 // 예금주
        private String usrId;                   // 로그인아이디
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankInfo {
        private String bankCd;                  // 은행코드
        private String bankAcntNo;              // 계좌번호
        private String dpstrNm;                 // 예금주
    }
}
