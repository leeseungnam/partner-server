package kr.wrightbrothers.apps.order;

import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.order.dto.PaymentCancelDto;
import kr.wrightbrothers.apps.order.service.PaymentService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController(value = "orderPaymentController")
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PaymentController extends WBController {

    private final PaymentService paymentService;

    @UserPrincipalScope
    @PutMapping("/payments/{orderNo}/cancel")
    public WBModel updatePaymentCancel(@Valid @RequestBody PaymentCancelDto paramDto) {
        // 결제 취소
        paymentService.updateCancelPayment(paramDto);

        return noneDataResponse();
    }

}