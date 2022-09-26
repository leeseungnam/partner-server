package kr.wrightbrothers.apps.category;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.category.service.CategoryService;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"카테고리"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CategoryController extends WBController {

    private final CategoryService categoryService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "access token", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "공통 카테고리 목록 조회", notes = "카테고리 SelectBox 조회를 위한 카테고리 정보 제공")
    @GetMapping("/categories")
    public WBModel findCategoryList(@ApiParam(value = "카테고리 그룹") @RequestParam String categoryGroup) {
        // 카테고리 목록 조회
        return defaultResponse(categoryService.findCategoryList(categoryGroup));
    }

}
