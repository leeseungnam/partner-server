package kr.wrightbrothers.apps.partner;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.constants.Email;
import kr.wrightbrothers.apps.common.constants.Partner;
import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.apps.email.service.EmailService;
import kr.wrightbrothers.apps.partner.dto.*;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.user.dto.UserDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.lang.WBCustomException;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"파트너"})
@Slf4j
@RestController()
@RequestMapping(value = "/v1/partner")
@RequiredArgsConstructor
public class PartnerController extends WBController {
    @Value("${app.client.host}")
    private String clientHost;
    private final String messagePrefix = "api.message.";
    private final MessageSourceAccessor messageSourceAccessor;
    private final PartnerService partnerService;
    private final ProductService productService;
    private final UserService userService;
    private final EmailService emailService;

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 등록", notes = "파트너 등록 요청 API 입니다.")
    @PostMapping("")
    public WBModel insertPartner(@ApiParam @Valid @RequestBody PartnerInsertDto paramDto) {

        // findBy사업자번호
        // 단위과세 사업자 번호만 중복 허용
        String businessNo = paramDto.getPartner().getBusinessNo();
        String businessClassificationCode = paramDto.getPartner().getBusinessClassificationCode();

        // 스토어명 중복체크.
        if(!partnerService.checkPartnerNameCount(paramDto.getPartner().getPartnerName())) throw new WBCustomException(ErrorCode.INVALID_PARTNER_NAME, messagePrefix+"common.duplication.custom", new String[] {messageSourceAccessor.getMessage(messagePrefix+"word.partner.name")});

        List<PartnerDto.ResBody> partnerList = partnerService.findPartnerListByBusinessNo(PartnerFindDto.Param.builder()
                        .businessNo(businessNo)
                        .build());

        // 단위과세 등록 인 경우 단위과세 아닌 타 사업유형의 경우 체크 사업자 번호 중복 체크
        if(Partner.Classification.UNIT_TAXPATER.getCode().equals(businessClassificationCode)) {
            partnerList.forEach(entity -> {
                if(!Partner.Classification.UNIT_TAXPATER.getCode().equals(entity.getBusinessClassificationCode()))
                    throw new WBCustomException(ErrorCode.INVALID_PARTNER_BISNO, messagePrefix+"common.duplication.custom", new String[] {messageSourceAccessor.getMessage(messagePrefix+"word.business.no")});
            });
        }else{
            // 단위과세 등록이 아닌 경우 복수 체크
            if(partnerList.size() > 0) throw new WBCustomException(ErrorCode.INVALID_PARTNER_BISNO, messagePrefix+"common.duplication.custom", new String[] {messageSourceAccessor.getMessage(messagePrefix+"word.business.no")});
        }
        // insertPartner
        partnerService.insertPartner(paramDto);

        String [] messageArgs = {messageSourceAccessor.getMessage(messagePrefix+"word.audit")+messageSourceAccessor.getMessage(messagePrefix+"word.request")};
        return  defaultMsgResponse(messageSourceAccessor, "common.complete.custom", messageArgs);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 권한 리스트 조회", notes = "파트너 권한 리스트 조회 요청 API 입니다.")
    @GetMapping("")
    public WBModel findPartnerList(@ApiIgnore @AuthenticationPrincipal UserPrincipal user) {

        // findUserAuth
        List<PartnerAndAuthFindDto.ResBody> partnerList = partnerService.findUserAuthAndPartnerListByUserId(PartnerAndAuthFindDto.Param.builder().userId(user.getUsername()).build());

        partnerList.forEach(entry -> {

            int productCount = 0;
            Object [] messageArgs = null;
            StringBuffer messageId = new StringBuffer();

            // 파트너 상태 운영중:1 일 경우 계약 상태 - 계약승인, 재계약(계약승인), 계약갱신
            if(Partner.Status.RUN.getCode().equals(entry.getPartnerStatus())) {
                productCount = productService.findProductCountByPartnerCode(entry.getPartnerCode());
                messageArgs = new Object[]{Integer.toString(productCount)};
            }

            messageId.append(messagePrefix)
                    .append("partner.comment.")
                    .append(entry.getPartnerStatus())
                    .append(".")
                    .append(entry.getContractStatus());

            entry.setComment(messageSourceAccessor.getMessage(messageId.toString(), messageArgs));

            // Name Set
            Partner.Status PartnerStatus = Partner.Status.valueOfCode(entry.getPartnerStatus());
            Partner.Contract.Status PartnerContractStatus = Partner.Contract.Status.valueOfCode(entry.getContractStatus());
            User.Auth UserAuth = User.Auth.valueOfCode(entry.getAuthCode());

            String displayName = PartnerStatus.getName();
            if((PartnerContractStatus.getCode().equals(Partner.Contract.Status.REQUEST.getCode())
                    || PartnerContractStatus.getCode().equals(Partner.Contract.Status.REJECT.getCode())
            ))
            {
                displayName = PartnerContractStatus.getName();
            }
            entry.setDisplayStatusName(displayName);
            entry.setAuthCodeName(UserAuth.getName());
        });

        return  defaultResponse(partnerList);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 정보 조회", notes = "파트너 정보 조회 요청 API 입니다.")
    @GetMapping("/{partnerCode}/{contractCode}")
    public WBModel findPartner(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode
                               ,@ApiParam(value = "계약 코드") @PathVariable String contractCode
    ) {

        return defaultResponse(partnerService.findPartnerByPartnerCode(PartnerViewDto.Param.builder()
                        .partnerCode(partnerCode)
                        .contractCode(contractCode)
                        .authCode(User.Auth.MANAGER.getType())  // 조회 대상 권한
                        .build()));
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 정보 수정(스토어 등록 포맷, 재심사요청 시 수정)", notes = "파트너 정보 수정 요청 API 입니다.")
    @PostMapping("/{partnerCode}/{contractCode}")
    public WBModel updatePartnerAll(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode
            ,@ApiParam(value = "계약 코드") @PathVariable String contractCode
            ,@ApiParam @Valid @RequestBody PartnerInsertDto paramDto) {

        PartnerDto.ResBody partnerDto = partnerService.findPartnerInfoByPartnerCode(partnerCode);

        // 스토어명 중복체크.
        if(!paramDto.getPartner().getPartnerName().equals(partnerDto.getPartnerName())) {
            if(!partnerService.checkPartnerNameCount(paramDto.getPartner().getPartnerName())) throw new WBCustomException(ErrorCode.INVALID_PARTNER_NAME, messagePrefix+"common.duplication.custom", new String[] {messageSourceAccessor.getMessage(messagePrefix+"word.partner.name")});
        }

        // findBy사업자번호
        // 단위과세 사업자 번호만 중복 허용
        if(!paramDto.getPartner().getBusinessNo().equals(partnerDto.getBusinessNo())) {
            String businessNo = paramDto.getPartner().getBusinessNo();
            String businessClassificationCode = paramDto.getPartner().getBusinessClassificationCode();

            List<PartnerDto.ResBody> partnerList = partnerService.findPartnerListByBusinessNo(PartnerFindDto.Param.builder()
                    .businessNo(businessNo)
                    .build());

            // 단위과세 등록 인 경우 단위과세 아닌 타 사업유형의 경우 체크 사업자 번호 중복 체크
            if(Partner.Classification.UNIT_TAXPATER.getCode().equals(businessClassificationCode)) {
                partnerList.forEach(entity -> {
                    if(!Partner.Classification.UNIT_TAXPATER.getCode().equals(entity.getBusinessClassificationCode()))
                        throw new WBCustomException(ErrorCode.INVALID_PARTNER_BISNO, messagePrefix+"common.duplication.custom", new String[] {messageSourceAccessor.getMessage(messagePrefix+"word.business.no")});
                });
            }else{
                // 단위과세 등록이 아닌 경우 복수 체크
                if(partnerList.size() > 0) throw new WBCustomException(ErrorCode.INVALID_PARTNER_BISNO, messagePrefix+"common.duplication.custom", new String[] {messageSourceAccessor.getMessage(messagePrefix+"word.business.no")});
            }
        }
        if(partnerService.findPartnerContract(partnerCode, contractCode).getContractStatus().equals(Partner.Contract.Status.REQUEST.getCode())) throw new WBCustomException(messagePrefix+"partner.comment."+Partner.Status.STOP.getCode()+"."+Partner.Contract.Status.REQUEST.getCode());

        paramDto.getPartner().changePartnerCode(partnerCode);
        paramDto.getPartnerContract().changePartnerCode(partnerCode);
        paramDto.getPartnerContract().changeContractCode(contractCode);

        // create user set
        partnerService.updatePartnerAll(paramDto);

        String [] messageArgs = {messageSourceAccessor.getMessage(messagePrefix+"word.audit")+messageSourceAccessor.getMessage(messagePrefix+"word.request")};
        return defaultMsgResponse(messageSourceAccessor, "common.complete.custom", messageArgs);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 섬네일 저장", notes = "파트너 섬네일 저장 요청 API 입니다.")
    @PostMapping("/{partnerCode}/thumbnail")
    public WBModel updatePartnerThumbnail(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode
                                          , @ApiParam(value = "섬네일 이미지 파일") @RequestParam MultipartFile multipartFile
                                          ,@ApiIgnore @AuthenticationPrincipal UserPrincipal user
    ) {
        WBModel response = new WBModel();

        log.info("[updatePartnerThumbnail]::partnerCode={}, file={}", partnerCode, multipartFile.getSize());

        response.addObject(WBKey.WBModel.DefaultDataKey, partnerService.savePartnerThumbnail(user.getUsername(), partnerCode, multipartFile).getFileSource());
        response.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(messagePrefix + "common.save.success"));

        return response;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 섬네일 삭제", notes = "파트너 섬네일 삭제 요청 API 입니다.")
    @DeleteMapping("/{partnerCode}/thumbnail")
    public WBModel deletePartnerThumbnail(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode
            ,@ApiIgnore @AuthenticationPrincipal UserPrincipal user
    ) {
        log.info("[deletePartnerThumbnail]::partnerCode={}", partnerCode);
        partnerService.deletePartnerThumbnail(user.getUsername(), partnerCode, null);

        return defaultMsgResponse(messageSourceAccessor, "common.complete");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 정보 수정(판매자 정보 포맷, 입점 담당자/알림톡 정보", notes = "파트너 정보 수정 요청 API 입니다.")
    @PutMapping("/{partnerCode}/{contractCode}")
    public WBModel updatePartner(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode
            ,@ApiParam(value = "계약 코드") @PathVariable String contractCode
            ,@ApiParam @Valid @RequestBody PartnerUpdateDto.ReqBody paramDto
            ,@ApiIgnore @AuthenticationPrincipal UserPrincipal user) {

        // create user set
        paramDto.changeUserId(user.getUsername());
        paramDto.changePartnerCode(partnerCode);
        paramDto.changeContractCode(contractCode);

        partnerService.updatePartner(paramDto);

        return defaultMsgResponse(messageSourceAccessor, "common.save.success");
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 운영자 초대", notes = "파트너 운영자 초대 요청 API 입니다.")
    @PostMapping("/{partnerCode}/operator")
    public WBModel invitePartnerOperator(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode
            ,@ApiParam @Valid @RequestBody PartnerInviteInsertDto paramDto
            ,@ApiIgnore @AuthenticationPrincipal UserPrincipal user
    ) {
        //  [todo] validation aop로 이동 처리 필요
        //  본인 계정 초대 확인
        if(user.getUsername().equals(paramDto.getPartnerOperator().getInviteReceiver()))
            throw new WBCustomException(messagePrefix+"partner.invite.fail.own");

        //  관리자 계정 초대 확인
        UserDto target = userService.findUserByUserIdAndPartnerCode(UserDto.builder()
                        .userId(paramDto.getPartnerOperator().getInviteReceiver())
                        .partnerCode(partnerCode)
                        .build()
        );
        if(!ObjectUtils.isEmpty(target)) {
            if(User.Auth.ADMIN.getType().equals(target.getAuthCode()))
                throw new WBCustomException(messagePrefix+"partner.invite.fail.admin");
        }

        //  초대 가능 인원 확인
        if(!partnerService.checkPartnerOperatorAuthCount(PartnerInviteDto.PartnerOperator.builder()
                .partnerCode(paramDto.getPartnerOperator().getPartnerCode())
                .authCode(paramDto.getPartnerOperator().getAuthCode())
                .build())
        ) {
            throw new WBCustomException(messagePrefix+"partner.invite.fail.max.sender"
                    , new String [] {
                            messageSourceAccessor.getMessage(messagePrefix+"word.user.status."+paramDto.getPartnerOperator().getAuthCode())
                                    ,messageSourceAccessor.getMessage(messagePrefix+"partner.invite.max.count")
                    });
        }
        //  이미 등록 된 운영자 확인
        if(!partnerService.checkPartnerOperatorCount(PartnerInviteDto.Param.builder()
                .partnerCode(paramDto.getPartnerOperator().getPartnerCode())
                .authCode(paramDto.getPartnerOperator().getAuthCode())
                .inviteReceiver(paramDto.getPartnerOperator().getInviteReceiver())
                .build()
            )
        ){
            throw new WBCustomException(messagePrefix+"common.already.insert.custom"
                    , new String[] {
                    messageSourceAccessor.getMessage(messagePrefix+"word.user.status."+paramDto.getPartnerOperator().getAuthCode())
            });
        }
        // 수락 안 한 초대 확인
        if(!partnerService.checkNotAcceptInviteCount(paramDto.getPartnerOperator())
        ){
            throw new WBCustomException(messagePrefix+"common.already.invite.custom"
                    , new String[] {
                    messageSourceAccessor.getMessage(messagePrefix+"word.user.status."+paramDto.getPartnerOperator().getAuthCode())
            });
        }

        //  insert invite
        String inviteCode = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
        paramDto.getPartnerOperator().changeInviteCode(inviteCode);
        paramDto.getPartnerOperator().changeInviteStatus("0");
        partnerService.insetPartnerOperator(paramDto);

        //  send email
        SingleEmailDto.ResBody resBody = emailService.singleSendEmail(SingleEmailDto.ReqBody.builder()
                        .emailType(Email.INVITE_OPERATOR.getCode())
                        .authCode(clientHost + "/partner/login?invite="+inviteCode)
                        .userId(paramDto.getPartnerOperator().getInviteReceiver())
                        .userName(paramDto.getPartnerOperator().getInviteReceiverName())
                        .build());

        return defaultMsgResponse(messageSourceAccessor, "partner.invite.send");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 운영자 초대 수락", notes = "파트너 운영자 초대 수락 요청 API 입니다.")
    @GetMapping("/invite/{inviteCode}")
    public WBModel invitePartnerOperator(@ApiParam(value = "운영자 초대 코드") @PathVariable String inviteCode
                                        ,@ApiIgnore @AuthenticationPrincipal UserPrincipal user
    ) {
        log.debug("[invitePartnerOperator]::inviteCode={}", inviteCode);

        //  set paramDto
        PartnerInviteDto.Param paramDto = PartnerInviteDto.Param.builder()
                .inviteCode(inviteCode)
                .inviteReceiver(user.getUsername())
                .inviteStatus(PartnerKey.INTSTRING_TRUE) // acceptInvite 초대 수락상태 set
                .build();
        //  select partnerCode, code, userId, inviteStatus
        PartnerInviteDto.ResBody inviteInfo = partnerService.findOperatorInvite(paramDto);

        //  초대 정보 확인
        if(ObjectUtils.isEmpty(inviteInfo)) throw new WBCustomException(messagePrefix+"common.not.exist.custom"
                , new String[] {messageSourceAccessor.getMessage(messagePrefix+"word.invite")});

        //  초대 된 대상 - 로그인 대상 일치여부 확인
        if(!inviteInfo.getInviteReceiver().equals(user.getUsername())) throw new WBCustomException(messagePrefix+"partner.invite.fail.diff.receiver");

        //  초대 가능 인원 확인
        if(!partnerService.checkPartnerOperatorAuthCount(PartnerInviteDto.PartnerOperator.builder()
                .partnerCode(inviteInfo.getPartnerCode())
                .authCode(inviteInfo.getAuthCode())
                .build())
        ) {
            throw new WBCustomException(messagePrefix+"partner.invite.fail.max.sender"
                    , new String[] {
                    messageSourceAccessor.getMessage(messagePrefix+"word.user.status."+inviteInfo.getAuthCode())
                    ,messageSourceAccessor.getMessage(messagePrefix+"partner.invite.max.count")
            });
        }

        if(!partnerService.checkPartnerOperatorCount(PartnerInviteDto.Param.builder()
                .partnerCode(inviteInfo.getPartnerCode())
                .authCode(inviteInfo.getAuthCode())
                .inviteReceiver(user.getUsername())
                .build())) {
            throw new WBCustomException(messagePrefix+"common.already.insert.custom"
                    , new String[] {
                    messageSourceAccessor.getMessage(messagePrefix+"word.user.status."+inviteInfo.getAuthCode())
            });
        }
        //  전 exception 중복처리?
        if(inviteInfo.getInviteStatus().equals(PartnerKey.INTSTRING_TRUE)) throw new WBCustomException(messagePrefix+"common.already.process.custom"
                , new String [] {messageSourceAccessor.getMessage(messagePrefix+"word.accept")});

        //  update invite and insert users_partner
        paramDto.changeAuthCode(inviteInfo.getAuthCode()); // 초대 받은 권한 set
        paramDto.changePartnerCode(inviteInfo.getPartnerCode()); // 초대 받은 파트너코드 set
        partnerService.acceptInvite(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "파트너 운영자 삭제", notes = "파트너 운영자 삭제 요청 API 입니다.")
    @DeleteMapping("/{partnerCode}/operator/{userId}")
    public WBModel deletePartnerOperator(@ApiParam(value = "파트너 코드") @PathVariable String partnerCode
                                         ,@ApiParam(value = "운영자 아이디") @PathVariable String userId
            ,@ApiIgnore @AuthenticationPrincipal UserPrincipal user
    ) {
        partnerService.deletePartnerOperator(PartnerInviteDto.Param.builder()
                        .partnerCode(partnerCode)
                        .authCode(User.Auth.MANAGER.getType())
                        .inviteReceiver(userId)
                .build());

        return noneMgsResponse(messageSourceAccessor);
    }
}
