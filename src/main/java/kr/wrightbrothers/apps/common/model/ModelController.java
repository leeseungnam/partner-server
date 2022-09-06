package kr.wrightbrothers.apps.common.model;

import kr.wrightbrothers.apps.common.model.service.ModelService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ModelController extends WBController {

    private final ModelService modelService;

    @GetMapping("/commons/brands/{brandNo}/models")
    public WBModel findModelList(@PathVariable String brandNo) {
        // 모델 목록 조회
        return defaultResponse(modelService.findModelList(brandNo));
    }
}
