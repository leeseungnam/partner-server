package kr.wrightbrothers.apps.template;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.template.dto.*;
import kr.wrightbrothers.apps.template.service.TemplateService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = {"템플릿"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TemplateController extends WBController {

    private final TemplateService templateService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "템플릿 목록 조회", notes = "등록된 템플릿을 목록 조회")
    @GetMapping("/templates")
    public WBModel findTemplateList(@ApiParam(value = "템플릿 타입") @RequestParam String[] templateType,
                                    @ApiParam(value = "페이지 행 수") @RequestParam int count,
                                    @ApiParam(value = "현재 페이지") @RequestParam int page,
                                    @ApiIgnore @AuthenticationPrincipal UserPrincipal user
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "템플릿 등록", notes = "템플릿 등록 기능 제공")
    @PostMapping("/templates")
    public WBModel insetTemplate(@ApiParam(value = "템플릿 등록 데이터") @Valid @RequestBody TemplateInsertDto paramDto) {
        // 추가 유효성 체크
        paramDto.validTemplate();

        // 템플릿 등록
        templateService.insertTemplate(paramDto);

        return noneDataResponse();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "템플릿 조회", notes = "템플릿 상세 내용 조회")
    @GetMapping("/templates/{templateNo}")
    public WBModel findTemplate(@ApiParam(value = "템플릿 번호") @PathVariable Long templateNo,
                                @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(
                templateService.findTemplate(
                        TemplateFindDto.Param.builder()
                                .partnerCode(user.getUserAuth().getPartnerCode())
                                .templateNo(templateNo)
                                .build())
        );

    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "템플릿 수정", notes = "등록된 템플릿 정보 수정")
    @PutMapping("/templates")
    public WBModel updateTemplate(@ApiParam(value = "템플릿 수정 데이터") @Valid @RequestBody TemplateUpdateDto paramDto) {
        // 추가 유효성 체크
        paramDto.validTemplate();

        // 템플릿 수정
        templateService.updateTemplate(paramDto);

        return noneDataResponse();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "템플릿 삭제", notes = "등록된 템플릿 정보 삭제")
    @DeleteMapping("/templates")
    public WBModel deleteTemplate(@ApiParam(value = "템플릿 번호") @RequestParam Long[] templateNoList,
                                  @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
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
