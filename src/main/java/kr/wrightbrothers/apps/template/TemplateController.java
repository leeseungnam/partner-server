package kr.wrightbrothers.apps.template;

import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.template.dto.*;
import kr.wrightbrothers.apps.template.service.TemplateService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TemplateController extends WBController {

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

    @PostMapping("/templates")
    public WBModel insetTemplate(@RequestBody TemplateInsertDto paramDto,
                                 @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.setPartnerCode(user.getUserAuth().getPartnerCode());

        // 템플릿 등록
        templateService.insertTemplate(paramDto);

        return noneDataResponse();
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

    @PutMapping("/templates")
    public WBModel updateTemplate(@RequestBody TemplateUpdateDto paramDto,
                                  @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.setPartnerCode(user.getUserAuth().getPartnerCode());

        // 템플릿 수정
        templateService.updateTemplate(paramDto);

        return noneDataResponse();
    }

    @DeleteMapping("/templates")
    public WBModel deleteTemplate(@RequestParam Long[] templateNoList,
                                  @AuthenticationPrincipal UserPrincipal user) {
        // 템플릿 등록 데이터 삭제
        templateService.deleteTemplate(
                TemplateDeleteDto.builder()
                        .templateNoList(templateNoList)
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .userId(user.getUsername())
                        .build()
        );

        return noneDataResponse();
    }

}
