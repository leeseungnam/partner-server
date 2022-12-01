package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.partner.dto.PartnerContractDto;
import kr.wrightbrothers.apps.partner.dto.PartnerContractSNSDto;
import kr.wrightbrothers.apps.partner.dto.PartnerInsertDto;
import kr.wrightbrothers.apps.partner.dto.PartnerUpdateDto;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.apps.queue.NotificationQueue;
import kr.wrightbrothers.apps.queue.PartnerQueue;
import kr.wrightbrothers.apps.user.dto.UserDto;
import kr.wrightbrothers.apps.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.parameters.P;

import java.util.Arrays;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class PartnerAfterAop {
    private final PartnerQueue partnerQueue;
    private final NotificationQueue notificationQueue;
    private final UserService userService;
    private final PartnerService partnerService;
    /**
     * <pre>
     *     스토어 등록, 수정의 작업 로직이 구현되어 있는 해당 상품 서비스 함수가 정상적으로 실행된 후
     *     아래 구현된 로직이 실행 됩니다.
     *
     *     해당 로직은 현재 등록 되어있는 스토어 정보를 조회하여 Admin 2.0 API 서버에 등록 또는 변동 된
     *     상품의 정보를 명세서에 맞게 조합 후 Message Queue 전송 처리를 합니다.
     *
     *     해당 전송 결과 로그는 Admin 2.0 모니터링 테이블에 결과가 수신되고 있으니 해당 테이블을 통하여
     *     결과를 참고하면 됩니다.
     *
     *     PartnerInsertDto Body 처리
     *     insertPartner, updatePartnerAll(partnerCode, contractCode)
     *     심사 요청(sns) -> return 심사결과 (sqs)
     *
     *     updatePartner(partnerCode, contractCode)
     *     수정 (sns)
     * </pre>
     */
    @AfterReturning(value = "execution(* kr.wrightbrothers.apps.partner.PartnerController.insert*(..)) ||"
            //  계약서 자동 시 갱신 추가
            +"execution(* kr.wrightbrothers.apps.partner.service.PartnerService.updateContractDay(..))"
    )
    public void sendPartnerSnsData(JoinPoint joinPoint) throws Exception {
        log.info("[sendPartnerSnsData]::Partner Send SNS.");

        boolean isSendNoti = false;
        String userPhone = "";
        String [] templateValue = new String[0];

        String partnerCode = "";
        String contractCode = "";
        DocumentSNS documentSNS = DocumentSNS.NULL;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object obj = Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow();

        if(obj instanceof PartnerInsertDto){

            PartnerInsertDto parmaDto = (PartnerInsertDto) obj;

            documentSNS = DocumentSNS.REQUEST_INSPECTION_PARTNER;
            partnerCode = parmaDto.getPartner().getPartnerCode();
            contractCode = parmaDto.getPartnerContract().getContractCode();

            // 파트너 등록 시 알림톡 발송 추가
            isSendNoti = true;
            UserDto user = userService.findUserByDynamic(UserDto.builder().userId(parmaDto.getPartner().getUserId()).build());
            userPhone = user.getUserPhone();
            templateValue = new String[]{parmaDto.getPartner().getPartnerName()};

        } else if (obj instanceof PartnerContractDto.ReqBody) {
            // updateContractDay
            PartnerContractDto.ReqBody paramDto = (PartnerContractDto.ReqBody) obj;

            documentSNS = DocumentSNS.UPDATE_PARTNER;
            partnerCode = paramDto.getPartnerCode();
            contractCode = paramDto.getContractCode();

        } else {
            log.info("[sendPartnerSnsData]::don't send partnerSnsData");
            return;
        }
        // partner data send
        partnerQueue.sendToAdmin(
                documentSNS
                , partnerCode
                , contractCode
                , methodSignature.getMethod().getName().contains("update") ?
                        PartnerKey.TransactionType.Update : PartnerKey.TransactionType.Insert
        );
        log.info("[sendPartnerSnsData]::sendToAdmin::partnerCode={}, contractCode={}", partnerCode, contractCode);

        if(isSendNoti) {
            log.info("[sendPartnerSnsData]::sendPushToAdmin::Send Partner Noti ... Start");
            notificationQueue.sendPushToAdmin(
                    DocumentSNS.NOTI_KAKAO_SINGLE
                    , Notification.REGISTER_STORE
                    , userPhone
                    , templateValue);
        }
        log.info("[sendPartnerSnsData]::sendPushToAdmin::userPhone={}", userPhone);
    }

    @AfterReturning(value = "execution(* kr.wrightbrothers.apps.partner.PartnerController.updatePartnerAll(..)) ||"
            + "execution(* kr.wrightbrothers.apps.partner.PartnerController.updatePartner(..))")
    public void sendUpdatePartnerSnsData(JoinPoint joinPoint) throws Exception {
        log.info("[sendUpdatePartnerSnsData]::Partner Send SNS.");

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Arrays.stream(joinPoint.getArgs()).forEach(obj -> {
            if(obj instanceof PartnerInsertDto){

                PartnerInsertDto parmaDto = (PartnerInsertDto) obj;

                DocumentSNS documentSNS = DocumentSNS.REQUEST_INSPECTION_PARTNER;
                String partnerCode = parmaDto.getPartner().getPartnerCode();
                String contractCode = parmaDto.getPartnerContract().getContractCode();

                // partner data send
                partnerQueue.sendToAdmin(
                        documentSNS
                        , partnerCode
                        , contractCode
                        , PartnerKey.TransactionType.Update
                );
                log.info("[sendUpdatePartnerSnsData]::sendToAdmin::partnerCode={}, contractCode={}", partnerCode, contractCode);

                // 파트너 등록 시 알림톡 발송 추가
                log.info("[sendUpdatePartnerSnsData]::sendPushToAdmin::Send Partner Noti ... Start");
                UserDto user = userService.findUserByDynamic(UserDto.builder().userId(parmaDto.getPartner().getUserId()).build());

                notificationQueue.sendPushToAdmin(
                        DocumentSNS.NOTI_KAKAO_SINGLE
                        , Notification.REGISTER_STORE
                        , user.getUserPhone()
                        , new String[]{parmaDto.getPartner().getPartnerName()});

                log.info("[sendUpdatePartnerSnsData]::sendPushToAdmin::userPhone={}", user.getUserPhone());
            } else if(obj instanceof PartnerUpdateDto.ReqBody){

                PartnerUpdateDto.ReqBody parmaDto = (PartnerUpdateDto.ReqBody) obj;

                DocumentSNS documentSNS = DocumentSNS.UPDATE_PARTNER;
                String partnerCode = parmaDto.getPartnerCode();
                String contractCode = parmaDto.getContractCode();

                // partner data send
                partnerQueue.sendToAdmin(
                        documentSNS
                        , partnerCode
                        , contractCode
                        , PartnerKey.TransactionType.Update
                );
                log.info("[sendPartnerSnsData]::sendToAdmin::partnerCode={}, contractCode={}", partnerCode, contractCode);

            } else {
                log.info("[sendPartnerSnsData]::don't send partnerSnsData");
                return;
            }
        });
    }
    //  섬네일 변경 시 추가 send to admin
    @AfterReturning(value = "execution(* kr.wrightbrothers.apps.partner.PartnerController.*PartnerThumbnail(..))")
    public void sendPartnerSnsDataByUpdateThumbnail(JoinPoint joinPoint) throws Exception {
        log.info("[sendPartnerSnsDataByUpdateThumbnail]::Partner Send SNS ... START");

        String partnerCode = (String) Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow();
        log.info("[sendPartnerSnsDataByUpdateThumbnail]::partnerCode={}",partnerCode);

        PartnerContractSNSDto partnerContractSNSDto = partnerService.findPartnerContractByPartnerCode(partnerCode);
        log.info("[sendPartnerSnsDataByUpdateThumbnail]::partnerCode={}, contractCode={}", partnerCode, partnerContractSNSDto.getContractCode());

        partnerQueue.sendToAdmin(
                DocumentSNS.UPDATE_PARTNER
                , partnerCode
                , partnerContractSNSDto.getContractCode()
                , PartnerKey.TransactionType.Update
        );
        log.info("[sendPartnerSnsDataByUpdateThumbnail]::Partner Send SNS ... END");
    }
}
