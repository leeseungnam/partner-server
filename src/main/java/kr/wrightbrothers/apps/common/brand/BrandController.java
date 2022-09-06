package kr.wrightbrothers.apps.common.brand;

import kr.wrightbrothers.apps.common.brand.service.BrandService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BrandController extends WBController {

    private final BrandService brandService;

    @GetMapping("/commons/brands")
    public WBModel findBrandList() {
        // 브랜드 목록 조회
        return defaultResponse(brandService.findBrandList());
    }

}
