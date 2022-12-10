package kr.wrightbrothers.apps.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import kr.wrightbrothers.apps.common.constants.DocumentSNS;
import kr.wrightbrothers.apps.queue.service.OrderQueueService;
import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderQueue extends WBSQS {

    private final WBAwsSns sender;
    @Value("${cloud.aws.sns.order}")
    private String topic;
    @Value("${cloud.aws.sqs.order}}")
    private String queueName;

    private final OrderQueueService orderQueueService;

    /**
     * 입점몰 API -> ADMIN 2.0 API
     * AWS SNS Message Queue 발송 처리
     *
     * @param documentSNS 문서 명
     * @param messages 전송 메시지
     * @param transactionType 전송 타입
     */
    public void sendToAdmin(DocumentSNS documentSNS,
                            Object messages,
                            String transactionType) {
        try {
            // AWS SNS 전송
            sender.send(
                    topic,
                    WBSnsDTO.Header.builder()
                            .docuNm(documentSNS.getName())
                            .trsctnTp(transactionType)
                            .build(),
                    messages
            );
        } catch (Exception e) {
            log.error("Order SNS Sender Error. {}", ExceptionUtils.getStackTrace(e));
        }
    }

    @SqsListener(value = "${cloud.aws.sqs.order}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void receiveFromAdmin(String message) {
        WBSnsDTO snsDto = null;

        try {
            // 초기화
            initMessage(message, queueName);
            snsDto = getSqsMessage(WBSnsDTO.class);
            WBSnsDTO.Header header = snsDto.getHeader();
            JSONParser parser = new JSONParser();
            JSONObject body = (JSONObject) parser.parse(new ObjectMapper().writeValueAsString(snsDto.getBody()));

            // 관련 키 포함여부 체크
            if (!body.containsKey("ordNo") & !body.containsKey("prnrCd") & !body.containsKey("ordPrdtStus")) return;
            // 반품요청 여부 확인
            if (!OrderConst.ProductStatus.REQUEST_RETURN.getCode().equals(body.get("ordPrdtStus"))) return;

            // 반품요청 푸시알림 처리
            log.info("Order SQS Receiver. OrderNo::{}, PartnerCode::{}, Order Product Status::{}",
                    body.get("ordNo"), body.get("prnrCd"), body.get("ordPrdtStus"));

            // 알림톡 전송
            orderQueueService.sendNotificationRequestReturn(String.valueOf(body.get("prnrCd")));

            ackMessage(snsDto);
        } catch (Exception e) {
            log.error("Order SQS Receiver Error. {}", ExceptionUtils.getStackTrace(e));
            ackMessage(snsDto, e);
        }

    }

}
