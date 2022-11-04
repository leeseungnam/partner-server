package kr.wrightbrothers.apps.queue;

import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.queue.service.PartnerQueueService;
import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO.Header;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerQueue extends WBSQS {

    private final WBAwsSns sender;
    private final PartnerQueueService partnerQueueService;
    @Value("${cloud.aws.sns.partner}")
    private String topic;
    @Value("${cloud.aws.sqs.partner}")
    private String queueName;

    /**
     * 입점몰 API -> ADMIN 2.0 API
     * AWS SNS Message Queue 발송 처리
     *
     * @param documentSNS 문서 명
     * @param partnerCode 파트너 코드
     * @param contractCode  계약 코드
     * @param transactionType 전송 타입 I/U
     */
    public void sendToAdmin(DocumentSNS documentSNS,
                            String partnerCode,
                            String contractCode,
                            String transactionType) {
        try {
            log.info("Partner SNS Sender Start");
            // AWS SNS 전송
            sender.send(
                    topic,
                    Header.builder()
                            .docuNm(documentSNS.getName())
                            .trsctnTp(transactionType)
                            .build(),
                    // 상품 SNS 전송 데이터
                    partnerQueueService.findPartnerSnsData(partnerCode, contractCode)
            );
            log.info("Partner SNS Sender END");
        } catch (Exception e) {
            log.error("Partner SNS Sender Error. {}", e.getMessage());
        }
    }

    /**
     * ADMIN 2.0 API -> 입점몰 API
     * AWS SQS Message Queue 수신 처리
     *
     * @param message SQS 스토어 수신 데이터
     */
    @SqsListener(value = "${cloud.aws.sqs.product}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void receiveFromAdmin(String message) {
        WBSnsDTO snsDto = null;

        try {
            initMessage(message, queueName);
            snsDto = getSqsMessage(WBSnsDTO.class);
            Header header = snsDto.getHeader();

            if (DocumentSNS.RESULT_INSPECTION.getName().equals(header.getDocuNm())) {

            }
        } catch (Exception e) {
            log.error("Partner SQS Receive Error. {}", e.getMessage());
            ackMessage(snsDto, e);
        }

    }
}
