package kr.wrightbrothers.apps.brand;

import kr.wrightbrothers.apps.brand.service.ModelService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ModelController extends WBController {

    private final ModelService modelService;

    @GetMapping("/brands/{brandNo}/models")
    public WBModel findModelList(@PathVariable String brandNo) {
        // 모델 목록 조회
        return defaultResponse(modelService.findModelList(brandNo));
    }
}
