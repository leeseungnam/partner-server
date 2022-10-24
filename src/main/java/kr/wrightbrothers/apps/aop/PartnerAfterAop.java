package kr.wrightbrothers.apps.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class PartnerAfterAop {
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
     *     현재 PointCut 영억은 패키지 partner -> service -> update*, insert*(PartnerService)
     *     함수 네이밍으로 되어있습니다. 추가 필요부분이 있을 경우 RequestBody 구조를 아래와 같이 하시기 바랍니다.
     *
     * </pre>
     */
    @AfterReturning(value =
                    "execution(* kr.wrightbrothers.apps.partner.PartnerController.update*(..)) ||" +
                    "execution(* kr.wrightbrothers.apps.partner.PartnerController.insert*(..))"
    )
    public void sendPartnerSnsData(JoinPoint joinPoint) throws Exception {
        // insertPartner, updatePartnerAll
        // 심사 요청(sns) -> return 심사결과 (sqs)

        // updatePartner
        // 수정 (sns)
    }
}
