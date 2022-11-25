package kr.wrightbrothers.apps.queue;

import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.queue.dto.NotificationSmsSendDto;
import kr.wrightbrothers.apps.queue.service.NotificationQueueService;
import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO.Header;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQueue extends WBSQS {

    private final WBAwsSns sender;
    private final NotificationQueueService notificationQueueService;
    @Value("${cloud.aws.sns.noti}")
    private String topic;

    /**
     * 입점몰 API -> ADMIN 2.0 API
     * AWS SNS Message Queue 발송 처리
     *
     * @param documentSNS 문서 명
     * @param notification 알림톡 정보 (messageId, messageType | ATA(알림톡), SMS(문자) .. , desc)
     */
    public void sendToAdmin(DocumentSNS documentSNS,
                            Notification notification,
                            String phone,
                            String text

    ) {
        try {
            log.info("Notification SNS Sender Start");
            // AWS SNS 전송
            sender.send(
                    topic,
                    Header.builder()
                            .docuNm(documentSNS.getName())
                            .trsctnTp(PartnerKey.TransactionType.Insert)
                            .build(),
                    // Notification SNS 전송 데이터
                    NotificationSmsSendDto.builder()
                            .toPhone(phone)
                            .text(text)
                            .snsDesc(notification.getDesc())
                            .build()
            );
            log.info("Notification SNS Sender END");
        } catch (Exception e) {
            log.error("Notification SNS Sender Error. {}", e.getMessage());
        }
    }
}
