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
