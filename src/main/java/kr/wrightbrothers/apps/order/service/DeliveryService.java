package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.constants.DocumentSNS;
import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.PartnerKey.WBDataBase.Alias;
import kr.wrightbrothers.apps.common.util.PartnerKey.WBDataBase.TransactionManager;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.queue.NotificationQueue;
import kr.wrightbrothers.apps.queue.OrderQueue;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final WBCommonDao dao;
    private final OrderQueue orderQueue;
    private final NotificationQueue notificationQueue;
    private final PaymentService paymentService;
    private final ResourceLoader resourceLoader;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Delivery.";
    private final String namespaceOrder = "kr.wrightbrothers.apps.order.query.Order.";

    public List<DeliveryListDto.Response> findDeliveryList(DeliveryListDto.Param paramDto,
                                                           boolean isRowBounds) {
        return isRowBounds ? dao.selectList(namespace + "findDeliveryList", paramDto, paramDto.getRowBounds(), Alias.Admin)
                : dao.selectList(namespace + "findDeliveryList", paramDto, Alias.Admin);
    }

    public DeliveryFindDto.Response findDelivery(DeliveryFindDto.Param paramDto) {
        return DeliveryFindDto.Response.builder()
                .order(dao.selectOne(namespaceOrder + "findOrder", paramDto.toOrderFindParam(), Alias.Admin))
                .payment(paymentService.findPaymentToOrder(paramDto.toOrderFindParam()))
                .deliveryList(dao.selectList(namespace + "findDeliveryProductList", paramDto, Alias.Admin))
                .build();
    }

    @Transactional(transactionManager = TransactionManager.Global)
    public void updateDeliveryFreight(DeliveryFreightUpdateDto paramDto) {
        if ((boolean) dao.selectOne(namespace + "isDeliveryComplete", paramDto, Alias.Admin))
            throw new WBBusinessException(ErrorCode.COMPLETE_DELIVERY.getErrCode(), new String[]{"화물배송"});
        if ((boolean) dao.selectOne(namespace + "isDeliveryParcel", paramDto, Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_DELIVERY_TYPE.getErrCode(), new String[]{"택배배송"});

        // MultiQuery
        // 화물배송 진행은 배송중의 단계가 없이 시작과 동시에 배송완료 상태 처리
        // 반품불가에 따른 분기처리에 대한 부분도 해당 SQL 처리 되어있으니 참고할 것.
        dao.update(namespace + "updateDeliveryFreight", paramDto, Alias.Admin);
        dao.update(namespaceOrder + "updateOrderStatusRefresh", paramDto.getOrderNo(), Alias.Admin);

        // SNS 주문상태 변경처리 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER_STATUS,
                paramDto.toQueueDto(),
                PartnerKey.TransactionType.Update
        );
    }

    @Transactional(transactionManager = TransactionManager.Global)
    public void updateDeliveryPickup(DeliveryPickupUpdateDto paramDto) {
        if ((boolean) dao.selectOne(namespace + "isDeliveryComplete", paramDto.toDeliveryInvoiceUpdateDto(), Alias.Admin))
            throw new WBBusinessException(ErrorCode.COMPLETE_DELIVERY.getErrCode(), new String[]{"방문수령"});

        // MultiQuery
        // 직접방문 수령은 배송방법 상관없이 배송완료 상태처리(배송테이블 상태는 수령으로 체크)
        dao.update(namespace + "updateDeliveryPickup", paramDto, Alias.Admin);
        dao.update(namespaceOrder + "updateOrderStatusRefresh", paramDto.getOrderNo(), Alias.Admin);

        // SNS 주문상태 변경처리 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER_STATUS,
                paramDto.toQueueDto(),
                PartnerKey.TransactionType.Update
        );
    }

    @Transactional(transactionManager = TransactionManager.Global)
    public void updateDeliveryInvoice(DeliveryInvoiceUpdateDto paramDto) {
        if ((boolean) dao.selectOne(namespace + "isDeliveryStart", paramDto.toDeliveryUpdateDto(), Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_DELIVERY_PREPARING.getErrCode(), new String[]{"상품준비중"});
        if ((boolean) dao.selectOne(namespace + "isDeliveryFreight", paramDto, Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_DELIVERY_TYPE.getErrCode(), new String[]{"화물배송"});

        // MultiQuery
        // 택배배송의 필요 정보인 택배사, 송장번호 변경 처리
        // 반품불가에 따른 배송 시 필요 주문상품 상태 변경 처리
        dao.update(namespace + "updateDeliveryInvoice", paramDto, Alias.Admin);

        // 송장번호 입력 알림톡 발송
        if (paramDto.getOrderProductSeqArray().length > 0) {
            DeliveryAddressDto.Param deliveryParam = DeliveryAddressDto.Param.builder().orderNo(paramDto.getOrderNo()).orderProductSeq(1).build();
            DeliveryAddressDto.Response delivery = dao.selectOne(namespace + "findDeliveryAddresses", deliveryParam, Alias.Admin);

            if (!ObjectUtils.isEmpty(delivery)) {
                if(delivery.getInvoiceNo() == null || "".equals(delivery.getInvoiceNo())) {
                    StringBuffer title = new StringBuffer();
                    title.append(paramDto.getProductName());
                    title = paramDto.getOrderProductSeqArray().length > 1 ? title.append(" 외 ").append(paramDto.getOrderProductSeqArray().length - 1).append("건") : title;
                    notificationQueue.sendPushToAdmin(
                            DocumentSNS.NOTI_KAKAO_SINGLE
                            , Notification.DELIVERY_START
                            , delivery.getRecipientPhone()
                            , new String[]{delivery.getRecipientName(), paramDto.getOrderNo(), title.toString(),
                                    paramDto.getDeliveryCompanyName(), paramDto.getInvoiceNo()});
                }
            }
        }

        // 상태 변경에 따른 SNS 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER,
                paramDto.toQueueDto(),
                PartnerKey.TransactionType.Update
        );
    }

    public void updateDeliveryMemo(DeliveryMemoUpdateDto paramDto) {
        dao.update(namespace + "updateDeliveryMemo", paramDto, Alias.Admin);
    }

    public void updateDelivery(DeliveryUpdateDto paramDto) {
        if ((boolean) dao.selectOne(namespace + "isDeliveryStart", paramDto, Alias.Admin))
            throw new WBBusinessException(ErrorCode.COMPLETE_DELIVERY.getErrCode(), new String[]{"배송정보"});

        dao.update(namespace + "updateDelivery", paramDto, Alias.Admin);
    }

    public void makeExcelFile(DeliveryExcelDto.Param paramDto,
                              HttpServletResponse response) throws IOException {
        ExcelUtil excel = new ExcelUtil(
                resourceLoader.getResource("classpath:templates/excel/deliveryList.xlsx").getInputStream(),
                1
        );

        if (ObjectUtils.isEmpty(paramDto.getDeliveryList())) {
            excel.excelWrite("배송목록리스트.xlsx", response);
            return;
        }

        List<DeliveryExcelDto.Response> deliveryList = dao.selectList(namespace + "findExcelDeliveryList", paramDto, Alias.Admin);

        // 엑셀 생성
        deliveryList.forEach(delivery -> {
            int colIndex = 23;
            // 병합 사용 처리에 대한 카운트 처리
            ++excel.mergeCount;
            ++excel.subMergeCount;

            excel.row = excel.sheet.createRow(excel.rowNumber++);

            excel.setCellValue(delivery);

            // 주문번호 기준 셀 병합처리
            if (excel.mergeCount == delivery.getOrderProductCount()) {
                if (excel.mergeCount > 1)
                    for (int col = 0; col <= colIndex; col++) {
                        if (col < 4 | (col > 10 & col < 13) | col > 16)
                            excel.sheet.addMergedRegion(new CellRangeAddress(excel.rowNumber - excel.mergeCount, excel.rowNumber - 1, col, col));
                    }

                excel.mergeCount = 0;
            }

            // 상품코드 기준 셀 병합처리
            if (excel.subMergeCount == delivery.getProductCount()) {
                if (excel.subMergeCount > 1)
                    for (int col = 0; col <= colIndex; col++) {
                        if (col > 3 & col <6)
                            excel.sheet.addMergedRegion(new CellRangeAddress(excel.rowNumber - excel.subMergeCount, excel.rowNumber - 1, col, col));
                    }

                excel.subMergeCount = 0;
            }
        });

        excel.excelWrite("배송목록리스트.xlsx", response);
    }

    public DeliveryAddressDto.Response findDeliveryAddresses(DeliveryAddressDto.Param paramDto) {
        return dao.selectOne(namespace + "findDeliveryAddresses", paramDto, Alias.Admin);
    }
}
