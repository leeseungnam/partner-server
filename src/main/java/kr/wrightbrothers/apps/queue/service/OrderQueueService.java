package kr.wrightbrothers.apps.queue.service;

import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueueService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.queue.query.Queue.";

    // 배송
    public List<Object> findOrderDeliverySnsData(String orderNo) {
        return dao.selectList(namespace + "findOrderDeliverySnsData", orderNo);
    }

}
