package kr.wrightbrothers.apps.queue;

import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
            log.error("Order SNS Sender Error. {}", e.getMessage());
        }
    }

}
