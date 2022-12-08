package kr.wrightbrothers.apps.template;

import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.template.dto.*;
import kr.wrightbrothers.apps.template.service.TemplateService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TemplateController extends WBController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final TemplateService templateService;

    @GetMapping("/templates")
    public WBModel findTemplateList(@RequestParam String[] templateType,
                                    @RequestParam int count,
                                    @RequestParam int page,
                                    @AuthenticationPrincipal UserPrincipal user
    ) {
        WBModel response = new WBModel();
        TemplateListDto.Param paramDto = TemplateListDto.Param.builder()
                .partnerCode(user.getUserAuth().getPartnerCode())
                .templateType(templateType)
                .count(count)
                .page(page)
                .build();

        // 템플릿 목록 조회
        response.addObject(WBKey.WBModel.DefaultDataKey, templateService.findTemplateList(paramDto));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

    @UserPrincipalScope
    @PostMapping("/templates")
    public WBModel insetTemplate(@Valid @RequestBody TemplateInsertDto paramDto) {
        // 추가 유효성 체크
        paramDto.validTemplate();

        // 템플릿 등록
        templateService.insertTemplate(paramDto);

        return insertMsgResponse(messageSourceAccessor);
    }

    @GetMapping("/templates/{templateNo}")
    public WBModel findTemplate(@PathVariable Long templateNo,
                                @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(
                templateService.findTemplate(
                        TemplateFindDto.Param.builder()
                                .partnerCode(user.getUserAuth().getPartnerCode())
                                .templateNo(templateNo)
                                .build())
        );

    }

    @UserPrincipalScope
    @PutMapping("/templates")
    public WBModel updateTemplate(@Valid @RequestBody TemplateUpdateDto paramDto) {
        // 추가 유효성 체크
        paramDto.validTemplate();

        // 템플릿 수정
        templateService.updateTemplate(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @DeleteMapping("/templates")
    public WBModel deleteTemplate(@RequestParam Long[] templateNoList,
                                  @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        // 템플릿 등록 데이터 삭제
        templateService.deleteTemplate(
                TemplateDeleteDto.builder()
                        .templateNoList(templateNoList)
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .userId(user.getUsername())
                        .build()
        );

        return noneMgsResponse(messageSourceAccessor);
    }

}
