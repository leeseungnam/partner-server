package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.partner.dto.PartnerContractDto;
import kr.wrightbrothers.apps.partner.dto.PartnerContractSNSDto;
import kr.wrightbrothers.apps.partner.dto.PartnerInsertDto;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.apps.queue.PartnerQueue;
import kr.wrightbrothers.framework.support.WBKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class PartnerAfterAop {
    private final PartnerQueue partnerQueue;

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
            +"execution(* kr.wrightbrothers.apps.partner.PartnerController.update*(..)) || "
            //  계약서 자동 시 갱신 추가
            +"execution(* kr.wrightbrothers.apps.partner.service.PartnerService.updateContractDay(..))"
    )
    public void sendPartnerSnsData(JoinPoint joinPoint) throws Exception {
        log.info("[sendPartnerSnsData]::Partner Send SNS.");

        AtomicReference<String> partnerCode = null;
        AtomicReference<String> contractCode = null;
        AtomicReference<DocumentSNS> documentSNS = null;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Arrays.stream(joinPoint.getArgs()).forEach(obj -> {

            if(obj instanceof PartnerInsertDto){
                PartnerInsertDto parmaDto = (PartnerInsertDto) obj;

                documentSNS.set(DocumentSNS.REQUEST_INSPECTION_PARTNER);
                partnerCode.set(parmaDto.getPartner().getPartnerCode());
                contractCode.set(parmaDto.getPartnerContract().getContractCode());

            } else if (obj instanceof PartnerContractDto.ReqBody) {
                PartnerContractDto.ReqBody paramDto = (PartnerContractDto.ReqBody) obj;

                documentSNS.set(DocumentSNS.UPDATE_PARTNER);
                partnerCode.set(paramDto.getPartnerCode());
                contractCode.set(paramDto.getContractCode());

            } else if (obj instanceof PartnerContractDto.ReqBody) {
                PartnerContractDto.ReqBody paramDto = (PartnerContractDto.ReqBody) obj;

                documentSNS.set(DocumentSNS.UPDATE_PARTNER);
                partnerCode.set(paramDto.getPartnerCode());
                contractCode.set(paramDto.getContractCode());

            } else {
                log.info("[sendPartnerSnsData]::don't send partnerSnsData");
                return;
            }
            partnerQueue.sendToAdmin(
                    documentSNS.get()
                    , partnerCode.get()
                    , contractCode.get()
                    , methodSignature.getMethod().getName().contains("update") ?
                            PartnerKey.TransactionType.Update : PartnerKey.TransactionType.Insert
            );
            log.info("[sendPartnerSnsData]::partnerCode={}, contractCode={}", partnerCode, contractCode);
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
