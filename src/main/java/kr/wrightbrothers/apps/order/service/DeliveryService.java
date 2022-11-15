package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
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
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final WBCommonDao dao;
    private final PaymentService paymentService;
    private final ResourceLoader resourceLoader;
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

                    // 배송정보 입력 시 배송 중 상태로 진행 처리(배송, 주문상품 배송중)
                    dao.update(namespace + "updateDeliveryStart", paramDto, PartnerKey.WBDataBase.Alias.Admin);
                });

        // 대표 상태코드 최신화 프로시져 호출
        dao.update("kr.wrightbrothers.apps.order.query.Order.updateOrderStatusRefresh", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin);
    }

    public void updateDeliveryMemo(DeliveryMemoUpdateDto paramDto) {
        // 배송메모 변경
        dao.update(namespace + "updateDeliveryMemo", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void updateDelivery(DeliveryUpdateDto paramDto) {
        // 상품준비중, 배송중 주문 상품의 배송지 정보 변경 처리
        dao.update(namespace + "updateDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void makeExcelFile(DeliveryExcelDto.Param paramDto,
                              HttpServletResponse response) throws IOException {
        // 엑셀 템플릿 초기화
        ExcelUtil excel = new ExcelUtil(
                new FileInputStream(resourceLoader.getResource("classpath:templates/excel/deliveryList.xlsx").getFile()),
                1
        );

        List<DeliveryExcelDto.Response> deliveryList = dao.selectList(namespace + "findExcelDeliveryList", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 엑셀 시트 생성
        excel.sheet = excel.workbook.getSheetAt(0);

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

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode("배송목록리스트.xlsx", StandardCharsets.UTF_8) + "\";");
        excel.workbook.write(response.getOutputStream());
        excel.workbook.close();
    }
}
