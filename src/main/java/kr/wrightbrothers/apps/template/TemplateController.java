package kr.wrightbrothers.apps.template;

import kr.wrightbrothers.apps.sign.dto.UserDetailDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.template.service.TemplateService;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/templates")
    private WBModel findTemplateList(@RequestParam String[] templateType,
                                     @AuthenticationPrincipal UserPrincipal user) {
        WBModel response = new WBModel();

        // 템플릿 목록 조회
        //response.addObject(WBKey.WBModel.DefaultDataKey, templateService.findTemplateList());

        return response;
    }

}
