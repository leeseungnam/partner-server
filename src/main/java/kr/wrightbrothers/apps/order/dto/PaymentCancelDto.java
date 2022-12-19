package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.constants.PaymentConst;
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

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelDto {
    /** 주문 번호 */
    @NotBlank(message = "주문 번호")
    private String orderNo;

    /** 주문 상품 SEQ Array */
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeq;

    /** 취소사유 코드 */
    @NotBlank(message = "취소사유 코드")
    private String cancelReasonCode;

    /** 취소사유 명 */
    @NotBlank(message = "취소사유 명")
    private String cancelReasonName;

    /** 결제방법 */
    @NotBlank(message = "결제방법")
    private String paymentMethodCode;

    /** 은행코드 */
    private String refundBankCode;

    /** 은행명 */
    private String refundBankName;

    /** 계좌번호 */
    @Size(min = 5, max = 30, message = "계좌번호")
    private String refundBankAccountNo;

    /** 예금주 */
    private String refundDepositorName;

    /** 파트너 코드 */
    private String partnerCode;

    /** 시영지 아이디 */
    @JsonIgnore
    private String userId;

    public void validRefundInfo() {
        // 무통장 결제 시 해당 유효성 체크
        if (!PaymentConst.Method.NON_BANK.getCode().equals(this.paymentMethodCode))
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

    public Queue toCancelQueueDto(BankInfo bankInfo,
                                  Long paymentAmount) {
        // Null Safe
        bankInfo = Optional.ofNullable(bankInfo).orElseGet(PaymentCancelDto.BankInfo::new);

        return Queue.builder()
                .ordNo(this.orderNo)
                .prnrCd(this.partnerCode)
                .ordPrdtIdx(Arrays.stream(this.orderProductSeq).map(String::valueOf).collect(Collectors.toList()))
                .cncRsnCd(this.cancelReasonCode)
                .cncRsn(this.cancelReasonName)
                .bankCd(bankInfo.getBankCd())
                .bankAcntNo(bankInfo.getBankAcntNo())
                .dpstrNm(bankInfo.getDpstrNm())
                .payAmt(paymentAmount)
                .rtrnDlvrAmt(0L)
                .refundAmt(paymentAmount)
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
        /** 주문번호 */
        private String ordNo;

        /** 파트너코드 */
        private String prnrCd;

        /** 상태코드 */
        private String stusCd;

        /** 주문상품 IDX LIST */
        private List<String> ordPrdtIdx;

        /** 취소 사유 코드 */
        private String cncRsnCd;

        /** 취소 사유 */
        private String cncRsn;

        /** 은행 코드 */
        private String bankCd;

        /** 계좌번호 */
        private String bankAcntNo;

        /** 예금주 */
        private String dpstrNm;

        /** 결제금액 */
        private Long payAmt;

        /** 반품배송금액 */
        private Long rtrnDlvrAmt;

        /** 환불예정금액 */
        private Long refundAmt;

        /** 사용자 아이디 */
        private String usrId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankInfo {
        /** 은행코드 */
        private String bankCd;

        /** 계좌번호 */
        private String bankAcntNo;

        /** 예금주 */
        private String dpstrNm;
    }
}
