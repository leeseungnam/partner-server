package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.constants.DocumentSNS;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
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
    private final PaymentService paymentService;
    private final ResourceLoader resourceLoader;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Delivery.";
    private final String namespaceOrder = "kr.wrightbrothers.apps.order.query.Order.";

    public List<DeliveryListDto.Response> findDeliveryList(DeliveryListDto.Param paramDto) {
        return dao.selectList(namespace + "findDeliveryList", paramDto, paramDto.getRowBounds(), PartnerKey.WBDataBase.Alias.Admin);
    }

    public DeliveryFindDto.Response findDelivery(DeliveryFindDto.Param paramDto) {
        return DeliveryFindDto.Response.builder()
                .order(dao.selectOne(namespaceOrder + "findOrder", paramDto.toOrderFindParam(), PartnerKey.WBDataBase.Alias.Admin))
                .payment(paymentService.findPaymentToOrder(paramDto.toOrderFindParam()))
                .deliveryList(dao.selectList(namespace + "findDeliveryProductList", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                .build();
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateDeliveryFreight(DeliveryFreightUpdateDto paramDto) {
        if (dao.selectOne(namespace + "isDeliveryComplete", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.COMPLETE_DELIVERY.getErrCode(), new String[]{"화물배송"});
        if (dao.selectOne(namespace + "isDeliveryParcel", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_DELIVERY_TYPE.getErrCode(), new String[]{"택배배송"});

        // MultiQuery
        // 화물배송 진행은 배송중의 단계가 없이 시작과 동시에 배송완료 상태 처리
        // 반품불가에 따른 분기처리에 대한 부분도 해당 SQL 처리 되어있으니 참고할 것.
        dao.update(namespace + "updateDeliveryFreight", paramDto, PartnerKey.WBDataBase.Alias.Admin);
        dao.update(namespaceOrder + "updateOrderStatusRefresh", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin);

        // SNS 주문상태 변경처리 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER_STATUS,
                paramDto.toQueueDto(),
                PartnerKey.TransactionType.Update
        );
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateDeliveryInvoice(DeliveryInvoiceUpdateDto paramDto) {
        if (dao.selectOne(namespace + "isDeliveryComplete", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.COMPLETE_DELIVERY.getErrCode(), new String[]{"송장번호"});
        if (dao.selectOne(namespace + "isDeliveryFreight", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_DELIVERY_TYPE.getErrCode(), new String[]{"화물배송"});

        // MultiQuery
        // 택배배송의 필요 정보인 택배사, 송장번호 변경 처리
        // 반품불가에 따른 배송 시 필요 주문상품 상태 변경 처리
        dao.update(namespace + "updateDeliveryInvoice", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 상태 변경에 따른 SNS 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER,
                paramDto.toQueueDto(),
                PartnerKey.TransactionType.Update
        );
    }

    public void updateDeliveryMemo(DeliveryMemoUpdateDto paramDto) {
        dao.update(namespace + "updateDeliveryMemo", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void updateDelivery(DeliveryUpdateDto paramDto) {
        if (dao.selectOne(namespace + "isDeliveryStart", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.COMPLETE_DELIVERY.getErrCode(), new String[]{"배송정보"});

        dao.update(namespace + "updateDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);
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

        List<DeliveryExcelDto.Response> deliveryList = dao.selectList(namespace + "findExcelDeliveryList", paramDto, PartnerKey.WBDataBase.Alias.Admin);

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
        return dao.selectOne(namespace + "findDeliveryAddresses", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }
}
