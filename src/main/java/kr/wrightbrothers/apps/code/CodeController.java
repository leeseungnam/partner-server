package kr.wrightbrothers.apps.code;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.code.service.CodeService;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"코드"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CodeController extends WBController {

    private final CodeService codeService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "access token", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "공통 코드 목록 조회", notes = "코드 SelectBox 조회를 위한 코드 정보 제공")
    @GetMapping("/master-code/{codeGroup}/codes")
    public WBModel findCodeList(@ApiParam(value = "코드 그룹") @PathVariable String codeGroup) {
        // 코드 목록 조회
        return defaultResponse(codeService.findCodeList(codeGroup));
    }

}
