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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        // 주문내역 목록 조회
        return dao.selectList(namespace + "findOrderList", paramDto, paramDto.getRowBounds(), PartnerKey.WBDataBase.Alias.Admin);
    }

    public OrderListDto.Statistics findOrderStatusStatistics(OrderListDto.Param paramDto) {
        // 주문내역 주문 집계 건수 조회
        return dao.selectOne(namespace + "findOrderStatusStatistics", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public OrderFindDto.Response findOrder(OrderFindDto.Param paramDto) {
        return OrderFindDto.Response.builder()
                // 주문내역 기본 정보
                .order(dao.selectOne(namespace + "findOrder", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                // 결제 정보
                .payment(paymentService.findPaymentToOrder(paramDto))
                // 주문 상품 리스트
                .productList(dao.selectList(namespace + "findOrderProduct", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                .build();
    }

    public void updateOrderMemo(OrderMemoUpdateDto paramDto) {
        // 송장번호 입력 시 배송지 정보 수정 제외
        dao.update(namespace + "updateOrderMemo", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void makeExcelFile(OrderExcelDto.Param paramDto,
                              HttpServletResponse response) throws IOException {

        // 엑셀 템플릿 사용하여 기본 설정
        ExcelUtil excel = new ExcelUtil(
                resourceLoader.getResource("classpath:templates/excel/orderList.xlsx").getInputStream(),
                1
        );

        List<OrderExcelDto.Response> orderList = dao.selectList(namespace + "findExcelOrderList", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 엑셀 시트 생성
        excel.sheet = excel.workbook.getSheetAt(0);

        // 엑셀 생성
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

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode("주문목록리스트.xlsx", StandardCharsets.UTF_8) + "\";");
        excel.workbook.write(response.getOutputStream());
        excel.workbook.close();
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updatePreparingDelivery(DeliveryPreparingDto paramDto) {
        // 싱테 준비중 변경 가능여부 체크
        if (dao.selectOne(namespace + "isNonOrderComplete", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_DELIVERY_PREPARING.getErrCode(), new String[]{"주문완료(결제완료)"});

        // 상품 준비중 변경(배송테이블 데이터 생성)
        dao.update(namespace + "updatePreparingDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 대표 상태코드 갱신
        dao.update(namespace + "updateOrderStatusRefresh", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin);

        // SNS 주문상태 변경처리 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER_STATUS,
                // Queue 전송 데이터 객체 변환
                paramDto.toQueueDto(dao.selectList(namespace + "findOrderProductSeq", paramDto, PartnerKey.WBDataBase.Alias.Admin)),
                PartnerKey.TransactionType.Update
        );
    }
}