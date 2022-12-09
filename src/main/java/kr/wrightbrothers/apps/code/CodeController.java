package kr.wrightbrothers.apps.code;

import kr.wrightbrothers.apps.code.service.CodeService;
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
public class CodeController extends WBController {

    private final CodeService codeService;

    @GetMapping("/master-code/{codeGroup}/codes")
    public WBModel findCodeList(@PathVariable String codeGroup) {
        return defaultResponse(codeService.findCodeList(codeGroup));
    }

}
