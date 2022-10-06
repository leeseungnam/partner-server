package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelDto {
    @NotBlank(message = "주문 번호")
    private String orderNo;                 // 주문 번호

    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeq;      // 주문 상품 SEQ

    @NotBlank(message = "취소사유 코드")
    private String cancelReasonCode;        // 취소사유 코드

    @NotBlank(message = "취소사유 명")
    private String cancelReasonName;        // 취소사유 명

    @NotBlank(message = "결제방법")
    private String paymentMethodCode;       // 결제방법

    private String refundBankCode;          // 은행코드
    private String refundBankName;          // 은행명

    @Size(min = 5, max = 30, message = "계좌번호")
    private String refundBankAccountNo;     // 계좌번호
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

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
    public void setAopUserId(String userId) {
        this.userId = userId;
    }
}
