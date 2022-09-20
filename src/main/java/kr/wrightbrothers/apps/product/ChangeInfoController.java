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

    @PatchMapping("/products")
    public WBModel updateProductStatus(@RequestBody StatusUpdateDto paramDto,
                                       @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.setPartnerCode(user.getUserAuth().getPartnerCode());

        // 상품 일괄 상태 변경
        changeInfoService.updateProductStatus(paramDto);

        return noneDataResponse();
    }

}
