package kr.wrightbrothers.apps.brand;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.brand.service.ModelService;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"모델"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ModelController extends WBController {

    private final ModelService modelService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "access token", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "브랜드 모델 목록 조회", notes = "모델 SelectBox 조회를 위한 브랜드에 해당하는 모델 정보 제공")
    @GetMapping("/brands/{brandNo}/models")
    public WBModel findModelList(@ApiParam(value = "브랜드 번호") @PathVariable String brandNo) {
        // 모델 목록 조회
        return defaultResponse(modelService.findModelList(brandNo));
    }
}
