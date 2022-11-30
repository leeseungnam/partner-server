package kr.wrightbrothers.apps.queue;

import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.queue.dto.NotificationSendDto;
import kr.wrightbrothers.apps.queue.dto.NotificationSmsSendDto;
import kr.wrightbrothers.apps.queue.service.NotificationQueueService;
import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO.Header;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

    /**
     * <pre>
     * 카카오 푸시알림 MQ 발송 Sender
     *
     * 카카오 Push 알림 요청을 Admin MQ로 데이터 전송 처리
     * 메시지 벨류 데이터는 템플릿 텍스트 안의 내용에 치완되어 사용되는 벨류값을
     * 순차적으로 배열 형식으로 요청할 것.
     *
     * 예)
     * ...
     * [라이트브라더스] 계약 갱신 안내
     * 안녕하세요 #{스토어명}님,
     *
     * 그동안 라이트브라더스와 함께해 주셔서 감사드립니다.
     * 라이트브라더스 판매자 계약이 60일 뒤에 만료되어 안내드립니다.
     *
     * ◼︎ 입점 계약 기간
     * #{계약시작일} ~ #{계약종료일}
     * ...
     *
     * notificationQueue.sendPushToAdmin(DocumentSNS.NOTI_KAKAO_SINGLE,
     *                                   Notification.CONTRACT_60DAY_PRIOR_END,
     *                                   "01047183922",
     *                                   partnerName,
     *                                   contractStartDay,
     *                                   contractEndDay);
     * ...
     * </pre>
     *
     * @param documentSNS 도큐멘트
     * @param notification 발송 템플릿
     * @param to 수신자
     * @param messageValue 메시지 벨류 데이터 -> Arrays String
     */
    public void sendPushToAdmin(DocumentSNS documentSNS,
                                Notification notification,
                                String to,
                                String... messageValue) {
        try {
            // 카카오 푸시알림 전송
            sender.send(
                    topic,
                    Header.builder()
                            .docuNm(documentSNS.getName())
                            .trsctnTp(PartnerKey.TransactionType.Insert)
                            .build(),
                    // Notification Kakao 전송 데이터
                    NotificationSendDto.builder()
                            .toPhone(to)
                            .templateId(notification.getMessageId())
                            .templateValue(messageValue)
                            .build()
            );
            log.info("Notification Kakao Push Sender Complete. MessageId::{}, To::{}", notification.getMessageId(), to);
        } catch (Exception e) {
            log.error("Notification Kakao Push Sender Error. {}", ExceptionUtils.getStackTrace(e));
        }
    }

}
