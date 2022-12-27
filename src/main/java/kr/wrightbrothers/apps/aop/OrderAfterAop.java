package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.common.constants.DocumentSNS;
import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.constants.PaymentConst;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.PaymentCancelDto;
import kr.wrightbrothers.apps.queue.HistoryQueue;
import kr.wrightbrothers.apps.queue.service.OrderQueueService;
import kr.wrightbrothers.framework.util.JsonUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class OrderAfterAop {

    private final HistoryQueue historyQueue;
    private final OrderQueueService orderQueueService;

    @AfterReturning(value =
            "execution(* kr.wrightbrothers.apps.order.service.PaymentService.updateCancelPayment(..))"
    )
    public void sendOrderKakao(JoinPoint joinPoint) {

        Arrays.stream(joinPoint.getArgs()).forEach(object -> {

            // 주문취소 요청 푸시알림
            if (object instanceof PaymentCancelDto) {

                PaymentCancelDto paramDto = (PaymentCancelDto) object;

                switch (PaymentConst.Method.of(paramDto.getPaymentMethodCode())) {
                    case NON_BANK:
                        orderQueueService.sendNotificationKakao(paramDto.getPartnerCode(), Notification.REQUEST_CANCEL_ORDER);
                        log.info("Order Request Cancel Notification. PartnerCode::{}, OrderNo::{}", paramDto.getPartnerCode(), paramDto.getOrderNo());
                        break;
                }

            }

        });

    }

    /**
     * <pre>
     *     주문, 배송, 반품 서비스의 수정 로직이 구현되어 있는 해당 함수가 정상적으로 실행된 후
     *     아래 구현된 로직이 실행 됩니다.
     *
     *     해당 로직은 현재 등록 되어있는 스토어 주문 정보를 조회하여 Admin 2.0 API 서버에 변동 된
     *     주문 정보를 명세서에 맞게 조합 후 Message Queue 전송 처리를 하여 주문 이력을 등록하게 합니다.
     *
     *     해당 전송 결과 로그는 Admin 2.0 모니터링 테이블에 결과가 수신되고 있으니 해당 테이블을 통하여
     *     결과를 참고하면 됩니다.
     *
     *     현재 PointCut 영역은 패키지 order -> service -> *Service.java -> update* method
     * </pre>
     */
    @AfterReturning(value =
            "execution(* kr.wrightbrothers.apps.order.service.*Service.update*(..)))"
    )
    public void sendHistoryQueue(JoinPoint joinPoint) throws Exception {
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        if (!object.has("orderNo")) return;

        log.info("Order History Send SNS. Order No::{},", object.getString("orderNo"));
        historyQueue.sendToAdmin(
                DocumentSNS.UPDATE_HISTORY,
                HistoryMessages.builder()
                        .appKey(object.getString("orderNo"))
                        .appNm(DocumentSNS.UPDATE_ORDER.getName())
                        .build(),
                PartnerKey.TransactionType.Update
        );
    }

    @Getter
    @Builder
    static class HistoryMessages {
        private String appKey;
        private String appNm;
        private Object data;
    }
}
