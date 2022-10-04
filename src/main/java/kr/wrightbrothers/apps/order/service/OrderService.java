package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.order.dto.OrderFindDto;
import kr.wrightbrothers.apps.order.dto.OrderListDto;
import kr.wrightbrothers.apps.order.dto.OrderUpdateDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final WBCommonDao dao;
    private final PaymentService paymentService;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Order.";

    public List<OrderListDto.Response> findOrderList(OrderListDto.Param paramDto) {
        // 주문내역 목록 조회
        return dao.selectList(namespace + "findOrderList", paramDto, paramDto.getRowBounds());
    }

    public OrderListDto.Statistics findOrderStatusStatistics(OrderListDto.Param paramDto) {
        // 주문내역 주문 집계 건수 조회
        return dao.selectOne(namespace + "findOrderStatusStatistics", paramDto);
    }

    public OrderFindDto.Response findOrder(OrderFindDto.Param paramDto) {
        return OrderFindDto.Response.builder()
                // 주문내역 기본 정보
                .order(dao.selectOne(namespace + "findOrder", paramDto.getOrderNo()))
                // 결제 정보
                .payment(paymentService.findPaymentToOrder(paramDto.getOrderNo()))
                // 주문 상품 리스트
                .productList(dao.selectList(namespace + "findOrderProduct", paramDto.getOrderNo()))
                .build();
    }

    public void updateOrder(OrderUpdateDto paramDto) {
        // 송장번호 입력 시 배송지 정보 수정 제외
        dao.update(namespace + "updateOrder", paramDto);
    }
}