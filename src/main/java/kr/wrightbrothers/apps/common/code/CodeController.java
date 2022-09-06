package kr.wrightbrothers.apps.common.code;

import kr.wrightbrothers.apps.common.code.service.CodeService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CodeController extends WBController {

    private final CodeService codeService;

    @GetMapping("/commons/master-code/{codeGroup}/codes")
    public WBModel findCodeList(@PathVariable String codeGroup) {
        // 코드 목록 조회
        return defaultResponse(codeService.findCodeList(codeGroup));
    }

}