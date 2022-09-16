package kr.wrightbrothers.apps.brand;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.wrightbrothers.apps.brand.service.BrandService;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"브랜드"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class BrandController extends WBController {

    private final BrandService brandService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "access token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "브랜드 목록 조회", notes = "브랜드 SelectBox 조회를 위한 번호, 브랜드 명을 제공")
    @GetMapping("/brands")
    public WBModel findBrandList() {
        // 브랜드 목록 조회
        return defaultResponse(brandService.findBrandList());
    }

}
