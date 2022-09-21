package kr.wrightbrothers.apps.product;

import kr.wrightbrothers.apps.product.dto.ChangeInfoListDto;
import kr.wrightbrothers.apps.product.dto.StatusUpdateDto;
import kr.wrightbrothers.apps.product.service.ChangeInfoService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ChangeInfoController extends WBController {

    private final ChangeInfoService changeInfoService;

    @GetMapping("/products/{productCode}/change-history")
    public WBModel findProductChangeHistory(@PathVariable String productCode,
                                            @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(
                // 상품 상태 변경 이력
                changeInfoService.findProductChangeHistory(
                        ChangeInfoListDto.Param.builder()
                                .partnerCode(user.getUserAuth().getPartnerCode())
                                .productCode(productCode)
                                .build()
                ));
    }


}
