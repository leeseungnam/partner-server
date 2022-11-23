package kr.wrightbrothers.apps.order.service;

import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.queue.OrderQueue;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.lang.WBException;
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
public class DeliveryService {

    private final WBCommonDao dao;
    private final OrderQueue orderQueue;
    private final PaymentService paymentService;
    private final ResourceLoader resourceLoader;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Delivery.";
    private final String namespaceOrder = "kr.wrightbrothers.apps.order.query.Order.";

    public List<DeliveryListDto.Response> findDeliveryList(DeliveryListDto.Param paramDto) {
        // 배송관리 목록 조회
        return dao.selectList(namespace + "findDeliveryList", paramDto, paramDto.getRowBounds(), PartnerKey.WBDataBase.Alias.Admin);
    }

    public DeliveryFindDto.Response findDelivery(DeliveryFindDto.Param paramDto) {
        return DeliveryFindDto.Response.builder()
                // 주문 기본 정보
                .order(dao.selectOne(namespaceOrder + "findOrder", OrderFindDto.Param.builder()
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
                .build();
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateDeliveryFreight(DeliveryFreightUpdateDto paramDto) {
        // 요청 주문 상품 목록에 배송 진행된 상품 유무 확인
        if (dao.selectOne(namespace + "isDeliveryComplete", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.COMPLETE_DELIVERY.getErrCode(), new String[]{"화물배송"});
        // 택배 포함 여부 확인
        if (dao.selectOne(namespace + "isDeliveryParcel", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_DELIVERY_TYPE.getErrCode(), new String[]{"택배배송"});

        // 화물배송 배송 등록(Multi Query)
        dao.update(namespace + "updateDeliveryFreight", paramDto, PartnerKey.WBDataBase.Alias.Admin);
        // 주문 대표 상태코드 갱신
        dao.update(namespaceOrder + "updateOrderStatusRefresh", paramDto.getOrderNo(), PartnerKey.WBDataBase.Alias.Admin);

        // 상태 변경에 따른 SNS 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER_STATUS,
                paramDto.toQueueDto(),
                PartnerKey.TransactionType.Update
        );
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateDeliveryInvoice(DeliveryInvoiceUpdateDto paramDto) {
        // 요청 주문 상품 목록에 배송 진행된 상품 유무 확인
        if (dao.selectOne(namespace + "isDeliveryComplete", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.COMPLETE_DELIVERY.getErrCode(), new String[]{"송장번호"});
        // 화물배송 포함여부 확인
        if (dao.selectOne(namespace + "isDeliveryFreight", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_DELIVERY_TYPE.getErrCode(), new String[]{"화물배송"});

        // 택배사 정보(택배회사, 송장번호) 등록, 배송시작 상태 변경
        // 주문 배송, 주문 상품 Multi Query
        dao.update(namespace + "updateDeliveryInvoice", paramDto, PartnerKey.WBDataBase.Alias.Admin);

        // 상태 변경에 따른 SNS 전송
        orderQueue.sendToAdmin(
                DocumentSNS.UPDATE_ORDER,
                // Queue 전송 데이터 객체 변환
                paramDto.toQueueDto(),
                PartnerKey.TransactionType.Update
        );
    }

    public void updateDeliveryMemo(DeliveryMemoUpdateDto paramDto) {
        // 배송메모 변경
        dao.update(namespace + "updateDeliveryMemo", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void updateDelivery(DeliveryUpdateDto paramDto) {
        // 배송 시작 상품은 배송지 정보 변경 불가
        if (dao.selectOne(namespace + "isDeliveryStart", paramDto, PartnerKey.WBDataBase.Alias.Admin))
            throw new WBBusinessException(ErrorCode.COMPLETE_DELIVERY.getErrCode(), new String[]{"배송정보"});

        // 상품준비중 상품의 배송지 정보 변경 처리
        dao.update(namespace + "updateDelivery", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void makeExcelFile(DeliveryExcelDto.Param paramDto,
                              HttpServletResponse response) throws IOException {
        // 엑셀 템플릿 초기화
        ExcelUtil excel = new ExcelUtil(
                resourceLoader.getResource("classpath:templates/excel/deliveryList.xlsx").getInputStream(),
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

    public DeliveryAddressDto.Response findDeliveryAddresses(DeliveryAddressDto.Param paramDto) {
        return dao.selectOne(namespace + "findDeliveryAddresses", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }
}
