package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.partner.dto.PartnerAuthDto;
import kr.wrightbrothers.apps.partner.dto.PartnerInsertDto;
import kr.wrightbrothers.apps.partner.dto.PartnerUpdateDto;
import kr.wrightbrothers.apps.partner.dto.PartnerViewDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.lang.WBCustomException;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class PartnerBeforeAop {

    private final String messagePrefix = "api.message.";
    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.partner.query.Partner.";

    /**
     * 계정 소유의 스토어인지 유효성 체크
     */
    private void checkOwn(PartnerAuthDto paramDto, boolean isFind) {
        boolean isPartnerAuth = dao.selectOne(namespace + "checkPartnerAuth", paramDto);
        if (!isPartnerAuth) {
            log.error("Partner Own Error.");
            log.error("[checkPartnerAuth]::userId={}, authCode={}, partnerCode={}", paramDto.getUserId(), paramDto.getAuthCode(), paramDto.getPartnerCode());

            if(isFind) {
                throw new WBCustomException(ErrorCode.FORBIDDEN_REFRESH, messagePrefix+"common.forbidden", null);
            } else {
                throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
            }
        }
    }

    /*
    private UserPrincipal checkParam (PartnerAuthDto paramDto) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAuth = true;
        if(ObjectUtils.isEmpty(paramDto.getUserId()) && paramDto.getUserId().equals(user.getUserAuth().getUserId())){
            isAuth = false;
            log.error("Partner Own Error. userId");
            throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
        } else if(ObjectUtils.isEmpty(paramDto.getPartnerCode()) && paramDto.getPartnerCode().equals(user.getUserAuth().getPartnerCode())){
            isAuth = false;
            log.error("Partner Own Error. partnerCode");
            throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
        } else if(ObjectUtils.isEmpty(paramDto.getAuthCode()) && paramDto.getAuthCode().equals(user.getUserAuth().getAuthCode())){
            isAuth = false;
            log.error("Partner Own Error. authCode");
            throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
        }
        if(!isAuth) throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());

        return user;
    }
    */
    private void checkAuth (User.Auth UserAuth, String authCode) {
        if(!UserAuth.getType().equals(authCode))
            throw new WBBusinessException(ErrorCode.FORBIDDEN_REFRESH.getErrCode());
    }

    // 파트너 정보 권한 체크
    @Before(value ="execution(* kr.wrightbrothers.apps.partner.service.*Service.findPartnerByPartnerCode(..)) ||" +
//            insert 제한 X
//            "execution(* kr.wrightbrothers.apps.partner.service.*Service.insertPartner*(..)) ||" +
            "execution(* kr.wrightbrothers.apps.partner.service.*Service.updatePartner*(..))"
    )
    public void checkOwnPartner(JoinPoint joinPoint) throws Exception {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("[ownFindCheck]::userId={}, auth={}",user.getUsername(), user.getUserAuth().getAuthCode());

        // system login pass - partner sqs 에서 강제 set authentic
        if(User.Auth.SUPER.getType().equals(user.getUserAuth().getAuthCode())) {
            return;
        }

        Arrays.stream(joinPoint.getArgs()).forEach(entity -> log.info("[checkFindPartnerAuth]::{}",entity.toString()));

        if(Arrays.stream(joinPoint.getArgs()).findFirst().isPresent()){

            Object obj = Arrays.stream(joinPoint.getArgs()).findFirst().get();

            if(obj instanceof PartnerViewDto.Param){
                //  findPartner
                log.info("[checkOwnPartner]::instanceof findPartner");
                PartnerViewDto.Param parmaDto = (PartnerViewDto.Param) obj;

                checkOwn(PartnerAuthDto.builder()
                        .partnerCode(parmaDto.getPartnerCode())
                        .authCode(user.getUserAuth().getAuthCode())
                        .userId(user.getUsername())
                        .build(), true);

            } else if(obj instanceof PartnerInsertDto){
                //  updatePartnerAll, insertPartner (check X)
                log.info("[checkOwnPartner]::instanceof updatePartnerAll");
                PartnerInsertDto parmaDto = (PartnerInsertDto) obj;

                checkOwn(PartnerAuthDto.builder()
                        .partnerCode(parmaDto.getPartner().getPartnerCode())
                        .authCode(user.getUserAuth().getAuthCode())
                        .userId(user.getUsername())
                        .build(), false);

            } else if (obj instanceof PartnerUpdateDto.ReqBody) {
                //  updatePartner
                log.info("[checkOwnPartner]::instanceof updatePartner");
                PartnerUpdateDto.ReqBody parmaDto = (PartnerUpdateDto.ReqBody) obj;

                checkOwn(PartnerAuthDto.builder()
                        .partnerCode(parmaDto.getPartnerCode())
                        .authCode(user.getUserAuth().getAuthCode())
                        .userId(user.getUsername())
                        .build(), false);
            } else {
                log.info("[checkOwnPartner]::don't check own");
            }
        }
    }
}
