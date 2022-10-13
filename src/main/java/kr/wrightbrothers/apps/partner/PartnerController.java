package kr.wrightbrothers.apps.partner;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.constants.Partner;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.partner.dto.PartnerAndAuthFindDto;
import kr.wrightbrothers.apps.partner.dto.PartnerDto;
import kr.wrightbrothers.apps.partner.dto.PartnerFindDto;
import kr.wrightbrothers.apps.partner.dto.PartnerInsertDto;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Api(tags = {"파트너"})
@Slf4j
@RestController()
@RequestMapping(value = "/v1/partner")
@RequiredArgsConstructor
public class PartnerController extends WBController {
    private final String messagePrefix = "api.message.";
    private final MessageSourceAccessor messageSourceAccessor;
    private final PartnerService partnerService;

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "파트너 등록", notes = "파트너 등록 요청 API 입니다.")
    @PostMapping("")
    public WBModel insertPartner(@ApiParam @Valid @RequestBody PartnerInsertDto paramDto) {

        WBModel wbResponse = new WBModel();

        // findBy사업자번호
        // 단위과세 사업자 번호만 중복 허용
        String businessNo = paramDto.getPartner().getBusinessNo();
        String businessClassificationCode = paramDto.getPartner().getBusinessClassificationCode();

        List<PartnerDto.ResBody> partnerList = partnerService.findPartnerListByBusinessNo(PartnerFindDto.Param.builder()
                        .businessNo(businessNo)
                        .build());

        // 단위과세 등록이 아닌 경우 복수 체크
        if(!Partner.Classification.UNIT_TAXPATER.getCode().equals(businessClassificationCode)) {
            if(partnerList.size() > 0) throw new WBBusinessException(ErrorCode.DUPLICATION_OBJECT.getErrCode(), new String[]{messageSourceAccessor.getMessage(messagePrefix+"word.business.no")});

            // 단위과세 인 경우 단위과세 아닌 타 사업유형의 경우 체크
        }else{
            if(partnerList.size() > 0) {
                if(!ObjectUtils.isEmpty(partnerList.get(0)) && !Partner.Classification.UNIT_TAXPATER.getCode().equals(partnerList.get(0).getBusinessClassificationCode())) {
                    throw new WBBusinessException(ErrorCode.DUPLICATION_OBJECT.getErrCode(), new String[]{messageSourceAccessor.getMessage(messagePrefix+"word.business.no")});
                }
            }
        }
        // insertPartner
        partnerService.insertPartner(paramDto);

        Object [] messageArgs = {messagePrefix+"word.audit"+messagePrefix+"word.request"};
        wbResponse.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(messagePrefix+"common.complete", messageArgs));

        return  wbResponse;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "파트너 권한 리스트 조회", notes = "파트너 권한 리스트 조회 요청 API 입니다.")
    @GetMapping("")
    public WBModel findPartnerList(@ApiIgnore @AuthenticationPrincipal UserPrincipal user) {

        WBModel wbResponse = new WBModel();

        // findUserAuth
        List<PartnerAndAuthFindDto.ResBody> partnerList = partnerService.findUserAuthAndPartnerListByUserId(PartnerAndAuthFindDto.Param.builder().userId(user.getUsername()).build());

        wbResponse.addObject(WBKey.WBModel.DefaultDataKey, partnerList);
        return  wbResponse;
    }
}
