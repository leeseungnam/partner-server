package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.partner.dto.PartnerInsertDto;
import kr.wrightbrothers.apps.queue.PartnerQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class PartnerAfterAop {
    private final PartnerQueue partnerQueue;
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
            +"execution(* kr.wrightbrothers.apps.partner.PartnerController.update*(..))")
    public void sendPartnerSnsData(JoinPoint joinPoint) throws Exception {
        log.info("[sendPartnerSnsData]::Partner Send SNS.");

        Arrays.stream(joinPoint.getArgs()).forEach(obj -> {

            if(obj instanceof PartnerInsertDto){
                PartnerInsertDto parmaDto = (PartnerInsertDto) obj;

                String partnerCode = parmaDto.getPartner().getPartnerCode();
                String contractCode = parmaDto.getPartnerContract().getContractCode();

                partnerQueue.sendToAdmin(
                        DocumentSNS.REQUEST_INSPECTION_PARTNER
                        , partnerCode
                        , contractCode
                        , PartnerKey.TransactionType.Insert
                );
                log.info("[sendPartnerSnsData]::partnerCode={}, contractCode={}", partnerCode, contractCode);
            }
        });
    }
}
