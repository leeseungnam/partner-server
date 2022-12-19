package kr.wrightbrothers.apps.order;

import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.order.dto.PaymentCancelDto;
import kr.wrightbrothers.apps.order.dto.PaymentRefundDto;
import kr.wrightbrothers.apps.order.service.PaymentService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController(value = "orderPaymentController")
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PaymentController extends WBController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final PaymentService paymentService;

    @UserPrincipalScope
    @PutMapping("/payments/{orderNo}/cancel")
    public WBModel updatePaymentCancel(@Valid @RequestBody PaymentCancelDto paramDto) {
        // 결제 취소
        paymentService.updateCancelPayment(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @GetMapping("/payments/{orderNo}/refund-account/{orderProductSeq}")
    public WBModel findPaymentRefundAccount(@PathVariable String orderNo,
                                            @PathVariable Integer orderProductSeq,
                                            @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(paymentService.findPaymentRefundAccount(
                PaymentRefundDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .orderProductSeq(orderProductSeq)
                        .build()
        ));
    }

    @UserPrincipalScope
    @PutMapping("/payments/{orderNo}/refund-account")
    public WBModel updatePaymentRefundAccount(@Valid @RequestBody PaymentRefundDto.ReqBody paramDto, @PathVariable String orderNo) {
        // 환불 계좌정보 수정
        paymentService.updatePaymentRefundAccount(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

}