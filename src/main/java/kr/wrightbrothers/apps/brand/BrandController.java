package kr.wrightbrothers.apps.brand;

import kr.wrightbrothers.apps.brand.service.BrandService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class BrandController extends WBController {

    private final BrandService brandService;

    @GetMapping("/brands")
    public WBModel findBrandList() {
        return defaultResponse(brandService.findBrandList());
    }

}
