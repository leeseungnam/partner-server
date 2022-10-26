package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.OrderExcelDto;
import kr.wrightbrothers.apps.order.dto.OrderFindDto;
import kr.wrightbrothers.apps.order.dto.OrderListDto;
import kr.wrightbrothers.apps.order.dto.OrderMemoUpdateDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

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
                // 주문 상품 리스트(상품 준비중 상태)
                .productList(dao.selectList(namespace + "findOrderReadyProduct", paramDto, PartnerKey.WBDataBase.Alias.Admin))
                // 환불 입금 정보
                .refundBank(dao.selectOne(namespace + "findRefundBankInfo", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin))
                .build();
    }

    /**
     * <pre>
     * 배송 주문 상품 목록에서 송장번호가 입력 되어있으면, 해당 주문건에 대해서
     * 배송 진행이 되어있다고 판단 합니다.
     *
     * 배송 진행 상태일 경우 해당 배송지 정보는 제외하고 주문 메모 데이터만 수정
     * </pre>
     */
    public void updateOrder(OrderMemoUpdateDto paramDto) {
        // 송장번호 입력 시 배송지 정보 수정 제외
        dao.update(namespace + "updateOrder", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void makeExcelFile(OrderExcelDto.Param paramDto,
                              HttpServletResponse response) throws IOException {
        // 엑셀 템플릿 사용하여 기본 설정
        ExcelUtil excel = new ExcelUtil(
                new FileInputStream(resourceLoader.getResource("classpath:templates/excel/orderList.xlsx").getFile()),
                1
        );

        List<OrderExcelDto.Response> orderList = dao.selectList(namespace + "findExcelOrderList", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 엑셀 시트 생성
        excel.sheet = excel.workbook.getSheetAt(0);

        // 엑셀 생성
        orderList.forEach(order -> {
            int colIndex = 0;
            // 병합 사용 처리에 대한 카운트 처리
            ++excel.mergeCount;

            excel.row = excel.sheet.createRow(excel.rowNumber++);

            excel.setCellValue(colIndex++, order.getOrderDay());
            excel.setCellValue(colIndex++, order.getOrderNo());
            excel.setCellValue(colIndex++, order.getOrderUserName());
            excel.setCellValue(colIndex++, order.getOrderStatus());
            excel.setCellValue(colIndex++, order.getOrderName(), true);
            excel.setCellValue(colIndex++, order.getProductName(), true);
            excel.setCellValue(colIndex++, order.getProductOption(), true);
            excel.setCellValue(colIndex++, order.getProductQty());
            excel.setCellValue(colIndex++, order.getProductSellAmount());
            excel.setCellValue(colIndex++, order.getProductAmount());
            excel.setCellValue(colIndex++, order.getPaymentAmount());
            excel.setCellValue(colIndex++, order.getProductDeliveryChargeAmount());
            excel.setCellValue(colIndex++, order.getPaymentMethod());
            excel.setCellValue(colIndex++, order.getPaymentDay());
            excel.setCellValue(colIndex++, order.getPaymentStatus());
            excel.setCellValue(colIndex++, order.getCancelDay());
            excel.setCellValue(colIndex++, order.getCancelReason(), true);
            excel.setCellValue(colIndex++, order.getAddress(), true);
            excel.setCellValue(colIndex++, order.getRequestDetail(), true);
            excel.setCellValue(colIndex, order.getOrderMemo(), true);

            // 셀 병합처리
            if (excel.mergeCount == order.getProductCount()) {
                if (excel.mergeCount > 1)
                    for (int col = 0; col <= colIndex; col++) {
                        if (col < 5 | col == 10 | (col > 11 & col < 15) | (col > 16 & col < 19))
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
}