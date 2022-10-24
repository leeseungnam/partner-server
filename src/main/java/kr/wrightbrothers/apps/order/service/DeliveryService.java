package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final WBCommonDao dao;
    private final PaymentService paymentService;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Delivery.";

    public List<DeliveryListDto.Response> findDeliveryList(DeliveryListDto.Param paramDto) {
        // 배송관리 목록 조회
        return dao.selectList(namespace + "findDeliveryList", paramDto, paramDto.getRowBounds(), PartnerKey.WBDataBase.Alias.Admin);
    }

    public DeliveryFindDto.Response findDelivery(DeliveryFindDto.Param paramDto) {
        return DeliveryFindDto.Response.builder()
                // 주문 기본 정보
                .order(dao.selectOne("kr.wrightbrothers.apps.order.query.Order.findOrder", OrderFindDto.Param.builder()
                                .partnerCode(paramDto.getPartnerCode())
                                .orderNo(paramDto.getOrderNo())
                        .build(), PartnerKey.WBDataBase.Alias.Admin))
                // 결제 정보
                .payment(paymentService.findPaymentToOrder(OrderFindDto.Param.builder()
                                .partnerCode(paramDto.getPartnerCode())
                                .orderNo(paramDto.getOrderNo())
                        .build()))
                // 배송 주문 상품 리스트
                .deliveryList(dao.selectList(namespace + "findDeliveryProductList", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                // 배송 완료 주문 상품 리스트
                .deliveryCompleteList(dao.selectList(namespace + "findDeliveryCompleteProductList", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                .build();
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateDeliveryInvoice(DeliveryInvoiceUpdateDto paramDto) {
        Arrays.stream(paramDto.getOrderProductSeqArray())
                .forEach(orderProductSeq -> {
                    // 주문 상품 SEQ 설정
                    paramDto.setOrderProductSeq(orderProductSeq);

                    // 배송정보 입력 가능 상태 체크
                    // 시스템 에러가 아닌 이상 다른 상태 코드는 패스
                    if (dao.selectOne(namespace + "isDeliveryInvoiceCheck", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                        return;

                    // 배송정보 입력 처리
                    dao.update(namespace + "updateDeliveryInvoice", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                    // 배치 프로그램에서 해당 상태 배송중으로 변경처리 할 것.
                    // 주문 상품 배송중 상태값 변경
                    // dao.update(namespace + "updateProductDeliveryStartStatus", paramDto);
                });

        /*
         * 송장번호 입력 후 어드민 배치 프로세스 처리에서 주문 상태 정보에 대한 처리를 실행 함.
         * 만약 입점몰에서 어느정도 상태 처리를 해야 한다라 하면 그때 아래 주석을 해제해야 함.
         *
         * 그 상황에 따라 아래 주석을 두고 QA 기간에도 변함이 없을 경우 해당 로직 삭제 처리.
         *
         */

        // 주문 배송에 대한 상태값 변경 가능 여부에 대한 유효성 체크
        // 상품준비중, 부분배송 상태값일 경우 다음 로직 진행, 이외 상태값은 종료
        // if (dao.selectOne("kr.wrightbrothers.apps.order.query.Order.isDeliveryStatusCheck", paramDto.getOrderNo()))
        //    return;

        // 주문 진행 상태 배송 상태 변경 처리
        // 주문 상품의 상태값이 상품준비중 데이터가 없을 경우 배송중으로 상태 처리
        // 상품준비중 데이터가 있을 경우 부분배송으로 상태 처리되고 그 이외의 값은 이미 위 조건에서 종료 처리.
        /*
        // dao.update("kr.wrightbrothers.apps.order.query.Order.updateOrderDeliveryStatus",
        //         OrderUpdateDto.Status.builder()
        //                .orderNo(paramDto.getOrderNo())
        //                .userId(paramDto.getUserId())
        //                .build()
        //        );
         */
    }

    /**
     * <pre>
     * 배송 주문 상품 목록에서 송장번호가 입력 되어있으면, 해당 주문건에 대해서
     * 배송 진행이 되어있다고 판단 합니다.
     *
     * 배송 진행 상태일 경우 해당 배송지 정보는 제외하고 배송 메모 데이터만 수정
     * </pre>
     */
    public void updateDelivery(DeliveryMemoUpdateDto paramDto) {
        // 송장번호 입력 시 배송지 정보 수정 제외
        dao.update(namespace + "updateDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }
}
