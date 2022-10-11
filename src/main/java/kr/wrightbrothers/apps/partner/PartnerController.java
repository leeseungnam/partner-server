package kr.wrightbrothers.apps.partner;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.partner.dto.PartnerInsertDto;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
    public WBModel insertPartner(@ApiParam @Valid @RequestBody PartnerInsertDto paramDto
            , HttpServletRequest request
            , HttpServletResponse response) {

        WBModel wbResponse = new WBModel();

        // findBy사업자번호
        if(!ObjectUtils.isEmpty(partnerService.findPartnerByBusinessNo(paramDto.getPartner().getBusinessNo()))) throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode());

        // insertPartner
        partnerService.insertPartner(paramDto);
        Object [] messageArgs = {"심사요청"};
        wbResponse.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(messagePrefix+"common.complete", messageArgs));

        return  wbResponse;
    }
}
