package kr.wrightbrothers.apps.temporary;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.temporary.dto.TemporaryDto;
import kr.wrightbrothers.apps.temporary.service.TemporaryService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TemporaryController extends WBController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final TemporaryService temporaryService;

    @GetMapping("/temporaries/{storageType}")
    public WBModel findTemporary(@PathVariable String storageType,
                                 @ApiIgnore @AuthenticationPrincipal UserPrincipal user)  {
        // 임시저장 데이터 조회
        return defaultResponse(temporaryService.findTemporary(new TemporaryDto.Param(
                user.getUserAuth().getPartnerCode(),
                user.getUsername(),
                storageType
        )));

    }

    @UserPrincipalScope
    @PostMapping("/temporaries")
    public WBModel mergeTemporary(@Valid @RequestBody TemporaryDto.ReqBody paramDto) throws JsonProcessingException {
        // 임시데이터 저장 처리
        temporaryService.mergeTemporary(paramDto);
        return defaultMsgResponse(messageSourceAccessor, "temporary.save.success", null);
    }

    @DeleteMapping("/temporaries")
    public WBModel deleteTemporary(@RequestParam String storageType,
                                   @AuthenticationPrincipal UserPrincipal user) {
        // 임시데이터 삭제 처리
        temporaryService.deleteTemporary(new TemporaryDto.Param(
                user.getUserAuth().getPartnerCode(),
                user.getUsername(),
                storageType
        ));

        return noneMgsResponse(messageSourceAccessor);
    }

}
