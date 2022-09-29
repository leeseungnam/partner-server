package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.order.dto.OrderListDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.product.query.Product.";

    public List<OrderListDto.Response> findOrderList(OrderListDto.Param paramDto) {
        // 주문내역 목록 조회
        return dao.selectList(namespace + "findOrderList", paramDto, paramDto.getRowBounds());
    }

    public OrderListDto.Statistics findOrderStatusStatistics(OrderListDto.Param paramDto) {
        // 주문내역 주문 집계 건수 조회
        return dao.selectOne(namespace + "findOrderStatusStatistics", paramDto);
    }
}