package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.type.DocumentSNS;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final WBCommonDao dao;
    private final OrderQueue orderQueue;
    private final PaymentService paymentService;
    private final ResourceLoader resourceLoader;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Order.";

    public List<OrderListDto.Response> findOrderList(OrderListDto.Param paramDto) {
        return dao.selectList(namespace + "findOrderList", paramDto, paramDto.getRowBounds(), PartnerKey.WBDataBase.Alias.Admin);
    }

    public OrderListDto.Statistics findOrderStatusStatistics(OrderListDto.Param paramDto) {
        return dao.selectOne(namespace + "findOrderStatusStatistics", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public OrderFindDto.Response findOrder(OrderFindDto.Param paramDto) {
        return OrderFindDto.Response.builder()
                .order(dao.selectOne(namespace + "findOrder", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                .payment(paymentService.findPaymentToOrder(paramDto))
                .productList(dao.selectList(namespace + "findOrderProduct", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                .build();
    }

    public void updateOrderMemo(OrderMemoUpdateDto paramDto) {
        dao.update(namespace + "updateOrderMemo", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void makeExcelFile(OrderExcelDto.Param paramDto,
                              HttpServletResponse response) throws IOException {
        ExcelUtil excel = new ExcelUtil(
                resourceLoader.getResource("classpath:templates/excel/orderList.xlsx").getInputStream(),
                1
        );

        List<OrderExcelDto.Response> orderList = dao.selectList(namespace + "findExcelOrderList", paramDto, PartnerKey.WBDataBase.Alias.Admin);
        excel.sheet = excel.workbook.getSheetAt(0);

        orderList.forEach(order -> {
            int colIndex = 20;
            // 병합 사용 처리에 대한 카운트 처리
            ++excel.mergeCount;

            excel.row = excel.sheet.createRow(excel.rowNumber++);
            excel.setCellValue(order);

            // 셀 병합처리
            if (excel.mergeCount == order.getProductCount()) {
                if (excel.mergeCount > 1)
                    for (int col = 0; col <= colIndex; col++) {
                        if (col < 5 | (col > 10 & col < 15) | (col > 16 & col < 20))
                            excel.sheet.addMergedRegion(new CellRangeAddress(excel.rowNumber - excel.mergeCount, excel.rowNumber - 1, col, col));
                    }

                excel.mergeCount = 0;
            }
        });

        excel.excelWrite("주문목록리스트.xlsx", response);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updatePreparingDelivery(DeliveryPreparingDto paramDto) {
        if (dao.selectOne(namespace + "isNonOrderComplete", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_DELIVERY_PREPARING.getErrCode(), new String[]{"주문완료(결제완료)"});

        // MultiQuery
        // 주문완료 상태에서 상품준비중에 필요한 상태의 데이터 등록 및 상태 변경 처리
        dao.update(namespace + "updatePreparingDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);
        dao.update(namespace + "updateOrderStatusRefresh", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin);

        // SNS 주문상태 변경처리 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER_STATUS,
                paramDto.toQueueDto(dao.selectList(namespace + "findOrderProductSeq", paramDto, PartnerKey.WBDataBase.Alias.Admin)),
                PartnerKey.TransactionType.Update
        );
    }
}