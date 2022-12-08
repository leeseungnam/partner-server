package kr.wrightbrothers.apps.category;

import kr.wrightbrothers.apps.category.service.CategoryService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CategoryController extends WBController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public WBModel findCategoryList(@RequestParam String categoryGroup) {
        // 카테고리 목록 조회
        return defaultResponse(categoryService.findCategoryList(categoryGroup));
    }

}
