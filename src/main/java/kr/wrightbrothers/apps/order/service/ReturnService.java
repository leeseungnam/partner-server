package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.order.dto.RequestReturnUpdateDto;
import kr.wrightbrothers.apps.order.dto.ReturnFindDto;
import kr.wrightbrothers.apps.order.dto.ReturnListDto;
import kr.wrightbrothers.apps.order.dto.ReturnMemoUpdateDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final WBCommonDao dao;
    private final PaymentService paymentService;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Return.";

    public List<ReturnListDto.Response> findReturnList(ReturnListDto.Param paramDto) {
        // 반품관리 목록 조회
        return dao.selectList(namespace + "findReturnList", paramDto, paramDto.getRowBounds());
    }

    public ReturnFindDto.Response findReturn(ReturnFindDto.Param paramDto) {
        String orderNamespace = "kr.wrightbrothers.apps.order.query.Order.";

        return ReturnFindDto.Response.builder()
                // 주문내역 기본 정보
                .order(dao.selectOne(orderNamespace + "findOrder", paramDto.getOrderNo()))
                // 결제 정보
                .payment(paymentService.findPaymentToOrder(paramDto.getOrderNo()))
                // 주문 상품 리스트
                .productList(dao.selectList(orderNamespace + "findOrderProduct", paramDto.getOrderNo()))
                // 반품 요청 상품 리스트
                .returnProductList(dao.selectList(namespace + "findReturnProductList", paramDto.getOrderNo()))
                .build();
    }

    /**
     * <pre>
     * 반품 관련해서 해당 부분의 송장번호가 입력 되어있으면, 배송지 정보 수정을 하지 않음.
     *
     * 반품에 관련된 사항이라 예외를 하여도 되지만,
     * 위 사항은 혹시 모를 사항을 생각하여 해당 부분 로직을 추가 처리.
     * </pre>
     */
    public void updateReturn(ReturnMemoUpdateDto paramDto) {
        // 송장번호 입력 시 배송지 정보 수정 제외
        dao.update(namespace + "updateReturn", paramDto);
    }

    public void updateRequestReturn(RequestReturnUpdateDto paramDto) {

        // 주문 상품 반품 상태값 변경 처리
        Arrays.stream(paramDto.getOrderProductSeqArray()).forEach(orderProductSeq -> {
            // 주문 상품 SEQ 설정
            paramDto.setOrderProductSeq(orderProductSeq);
            // 현재 주문 상품 상태 코드 조회
            String currentStatusCode = dao.selectOne(namespace + "findOrderProductStatusCode", paramDto);

            // 반품 요청 처리
            switch (OrderProductStatusCode.of(paramDto.getReturnProcessCode())) {
                case START_RETURN:
                case WITHDRAWAL_RETURN:
                    // 반품 요청이 아닐 경우 예외
                    if (!OrderProductStatusCode.REQUEST_RETURN.equals(OrderProductStatusCode.of(currentStatusCode)))
                        return;

                    // 주문 상품 반품 진행 / 반품 철회 처리
                    dao.update(namespace + "updateOrderProductReturnCode", paramDto);
                    break;
                case COMPLETE_RETURN:
                case NON_RETURN:
                    // 반품 진행이 아닐 경우 예외
                    if (!OrderProductStatusCode.START_RETURN.equals(OrderProductStatusCode.of(currentStatusCode)))
                        return;

                    // 주문 상품 반품 완료 / 반품 불가 처리
                    dao.update(namespace + "updateOrderProductReturnCode", paramDto);
                    break;
            }
        });

        // 주문 상태 변경 처리

        // 결제 상태 변경 처리

    }
}