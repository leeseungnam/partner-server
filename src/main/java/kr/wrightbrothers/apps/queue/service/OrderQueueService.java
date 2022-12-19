package kr.wrightbrothers.apps.queue.service;

import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.constants.DocumentSNS;
import kr.wrightbrothers.apps.queue.NotificationQueue;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueueService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.queue.query.Queue.";
    private final NotificationQueue notificationQueue;

    // 배송
    public List<Object> findOrderDeliverySnsData(String orderNo) {
        return dao.selectList(namespace + "findOrderDeliverySnsData", orderNo);
    }

    public void sendNotificationRequestReturn(String partnerCode) {
        // 파트너 이름 조회
        String partnerName = dao.selectOne(namespace + "findPartnerName", partnerCode);
        List<String> notificationList = dao.selectList(namespace + "findNotificationList", partnerCode);

        // 파트너 알림톡 수신등록 X
        if (ObjectUtils.isEmpty(notificationList)) log.info("Notification Target Empty. PartnerName::{}", partnerName);

        log.info("Order Request Return Notification. PartnerName::{}", partnerName);
        notificationList.forEach(notification -> {
            // 카카오 푸시알림 전송
            notificationQueue.sendPushToAdmin(
                    DocumentSNS.NOTI_KAKAO_SINGLE,
                    Notification.REQUEST_RETURN_ORDER,
                    notification,
                    partnerName);
        });
    }

}
