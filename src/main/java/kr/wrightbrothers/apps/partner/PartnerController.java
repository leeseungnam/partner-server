package kr.wrightbrothers.apps.partner;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.constants.Partner;
import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.partner.dto.*;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.apps.product.service.ProductService;
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
    private final ProductService productService;

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

        Object [] messageArgs = {messageSourceAccessor.getMessage(messagePrefix+"word.audit")+messageSourceAccessor.getMessage(messagePrefix+"word.request")};
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

        partnerList.forEach(entry -> {

            Partner.Status PartnerStatus = Partner.Status.valueOfCode(entry.getPartnerStatus());
            Partner.Contract.Status PartnerContractStatus = Partner.Contract.Status.valueOfCode(entry.getContractStatus());
            User.Auth UserAuth = User.Auth.valueOfCode(entry.getAuthCode());

            int productCount = 0;
            Object [] messageArgs = null;
            StringBuffer messageId = new StringBuffer();

            if(Partner.Status.COMPLETE_SUCESS.getCode().equals(entry.getPartnerStatus()) && Partner.Contract.Status.COMPLETE.getCode().equals(entry.getContractStatus())) {
                productCount = productService.findProductCountByPartnerCode(entry.getPartnerCode());
                messageArgs = new Object[]{Integer.toString(productCount)};
            }

            messageId.append(messagePrefix)
                    .append("partner.status.")
                    .append(entry.getPartnerStatus())
                    .append(".")
                    .append(entry.getContractStatus());

            entry.setComment(messageSourceAccessor.getMessage(messageId.toString(), messageArgs));

            entry.setPartnerStatusName(PartnerStatus.getName());
            entry.setContractStatusName(PartnerContractStatus.getName());
            entry.setAuthCodeName(UserAuth.getName());
        });

        wbResponse.addObject(WBKey.WBModel.DefaultDataKey, partnerList);
        return  wbResponse;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "파트너 정보 조회", notes = "파트너 정보 조회 요청 API 입니다.")
    @GetMapping("/{partnerCode}")
    public WBModel findPartner(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode) {

        return defaultResponse(partnerService.findPartnerByPartnerCode(PartnerViewDto.Param.builder()
                        .partnerCode(partnerCode)
                        .authCode(User.Auth.MANAGER.getType())
                        .build()));
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "파트너 정보 수정", notes = "파트너 정보 수정 요청 API 입니다.")
    @PostMapping("/{partnerCode}/{contractNo}")
    public WBModel updatePartnerAll(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode
            ,@ApiParam(value = "계약번호") @PathVariable String contractNo
            ,@ApiParam @Valid @RequestBody PartnerInsertDto paramDto
            ,@ApiIgnore @AuthenticationPrincipal UserPrincipal user) {

        WBModel wbResponse = new WBModel();

        paramDto.getPartner().changePartnerCode(partnerCode);
        paramDto.getPartnerContract().changePartnerCode(partnerCode);
        paramDto.getPartnerContract().changeContractNo(contractNo);

        // create user set
        partnerService.updatePartnerAll(paramDto);

        Object [] messageArgs = {messageSourceAccessor.getMessage(messagePrefix+"word.audit")+messageSourceAccessor.getMessage(messagePrefix+"word.request")};
        wbResponse.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(messagePrefix+"common.complete", messageArgs));

        return wbResponse;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "파트너 정보 수정", notes = "파트너 정보 수정 요청 API 입니다.")
    @PutMapping("/{partnerCode}/{contractNo}")
    public WBModel updatePartner(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode
            ,@ApiParam(value = "계약번호") @PathVariable String contractNo
            ,@ApiParam @Valid @RequestBody PartnerUpdateDto.ReqBody paramDto
            ,@ApiIgnore @AuthenticationPrincipal UserPrincipal user) {

        WBModel wbResponse = new WBModel();

        // create user set
        paramDto.changeUserId(user.getUsername());
        paramDto.changePartnerCode(partnerCode);
        paramDto.changeContractNo(contractNo);

        partnerService.updatePartner(paramDto);

        wbResponse.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(messagePrefix+"common.save.success"));
        return wbResponse;
    }
}