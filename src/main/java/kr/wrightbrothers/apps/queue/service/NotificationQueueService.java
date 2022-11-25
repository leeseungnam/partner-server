package kr.wrightbrothers.apps.queue.service;

import kr.wrightbrothers.apps.queue.dto.NotificationSendDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationQueueService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.partner.query.Noti.";

    public NotificationSendDto findNotificationSnsData(String messageId) {
        return NotificationSendDto.builder().build();
    }
}
