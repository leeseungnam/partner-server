package kr.wrightbrothers.apps.product.service;

import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.ProductUtil;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <pre>
 *     상품의 권한, 상태값 변경의 유효성 검사와, SNS 발송 처리에 대한 로직은 공통 되는 부분으로 해당 서비스에
 *     구현 내용이 없습니다. 관련 처리 로직은 전처리 AOP, 후처리 AOP 각각 분리하여 구현 하였으니 관련 부분 확인 부탁 드립니다.
 *
 *     ProductBeforeAop.java : 스토어 소유권 체크, 상픔 상태 변경 가능 검사
 *     ProductAfterAop.java : 스토어 상품 등록 / 변경 시 상품 정보 ADMIN 2.0 Message Queue 발송
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.product.query.Product.";
    private final ProductUtil productUtil;
    private final FileService fileService;
    private final ResourceLoader resourceLoader;
    private final ChangeInfoService changeInfoService;

    public List<ProductListDto.Response> findProductList(ProductListDto.Param paramDto) {
        // 상품목록 조회
        return dao.selectList(namespace + "findProductList" ,paramDto, paramDto.getRowBounds(), PartnerKey.WBDataBase.Alias.Admin);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void insertProduct(ProductInsertDto paramDto) {
        // 상품 기본정보 등록
        dao.insert(namespace + "insertProduct", paramDto.getProduct(), PartnerKey.WBDataBase.Alias.Admin);
        // 상품 기본스펙
        if (!ObjectUtils.isEmpty(paramDto.getBasicSpec())) {
            dao.insert(namespace + "mergeBasicSpec", paramDto.getBasicSpec(), PartnerKey.WBDataBase.Alias.Admin);
            // 연령 등록
            if (!ObjectUtils.isEmpty(paramDto.getBasicSpec().getAgeList()))
                dao.insert(namespace + "insertBasicSpecAge", paramDto.getBasicSpec(), PartnerKey.WBDataBase.Alias.Admin);
        }
        // 판매 정보
        dao.insert(namespace + "mergeSellInfo", paramDto.getSellInfo(), PartnerKey.WBDataBase.Alias.Admin);
        // 옵션 정보
        Optional.ofNullable(paramDto.getOptionList()).orElseGet(Collections::emptyList)
                .forEach(option -> dao.insert(namespace + "insertOption", option, PartnerKey.WBDataBase.Alias.Admin));
        // 배송 정보
        dao.insert(namespace + "mergeDelivery", paramDto.getDelivery(), PartnerKey.WBDataBase.Alias.Admin);
        // 정보 고시
        dao.insert(namespace + "mergeInfoNotice", paramDto.getInfoNotice(), PartnerKey.WBDataBase.Alias.Admin);
        // 안내 정보
        dao.insert(namespace + "mergeGuide", paramDto.getGuide(), PartnerKey.WBDataBase.Alias.Admin);
        // 변경 이력 등록
        changeInfoService.insertChangeInfo(paramDto.toChangeInfo());
        // 임시저장 파일 AWS S3 업로드
        fileService.s3FileUpload(paramDto.getFileList(), WBKey.Aws.A3.Product_Img_Path + paramDto.getProduct().getProductCode(), true);
    }

    public ProductFindDto.ResBody findProduct(ProductFindDto.Param paramDto) {
        List<OptionDto.ResBody> optionList = dao.selectList(namespace + "findOptionList", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin);
        ProductFindDto.ResBody findDto = ProductFindDto.ResBody.builder()
                // 상품 기본 정보
                .product(Optional.of((ProductDto.ResBody) dao.selectOne(namespace + "findProduct", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin)).orElse(new ProductDto.ResBody()))
                // 상품 기본스펙
                .basicSpec(Optional.of((BasicSpecDto.ResBody) dao.selectOne(namespace + "findBasicSpec", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin)).orElse(new BasicSpecDto.ResBody()))
                // 판매 정보
                .sellInfo(Optional.of((SellInfoDto.ResBody) dao.selectOne(namespace + "findSellInfo", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin)).orElse(new SellInfoDto.ResBody()))
                // 옵션 정보
                .optionList(Optional.ofNullable(optionList).orElseGet(Collections::emptyList))
                // 배송 정보
                .delivery(Optional.of((DeliveryDto.ResBody) dao.selectOne(namespace + "findDelivery", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin)).orElse(new DeliveryDto.ResBody()))
                // 정보 고시
                .infoNotice(Optional.of((InfoNoticeDto.ResBody) dao.selectOne(namespace + "findInfoNotice", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin)).orElse(new InfoNoticeDto.ResBody()))
                // 안내 정보
                .guide(Optional.of((GuideDto.ResBody) dao.selectOne(namespace + "findGuide", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin)).orElse(new GuideDto.ResBody()))
                .build();

        if (ProductStatusCode.REJECT_INSPECTION.getCode().equals(findDto.getSellInfo().getProductStatusCode()))
            // 반려 사유 조회
            findDto.setRejectReason(dao.selectOne(namespace + "findProductRejectReason", paramDto.getProductCode()));

        return findDto;
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateProduct(ProductUpdateDto paramDto) {
        // 상품 판매 기간 처리
        productUtil.updateProductSellDate(paramDto.getProductCode(), paramDto.getSellInfo().getProductStatusCode());

        // 상품 기본정보 수정
        dao.update(namespace + "updateProduct", paramDto.getProduct(), PartnerKey.WBDataBase.Alias.Admin);
        // 상품 기본스펙 수정
        if (!ObjectUtils.isEmpty(paramDto.getBasicSpec())) {
            dao.update(namespace + "mergeBasicSpec", paramDto.getBasicSpec(), PartnerKey.WBDataBase.Alias.Admin);
            // 연령 삭제
            dao.delete(namespace + "deleteBasicSpecAge", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin);
            // 연령 등록
            if (!ObjectUtils.isEmpty(paramDto.getBasicSpec().getAgeList()))
                dao.insert(namespace + "insertBasicSpecAge", paramDto.getBasicSpec(), PartnerKey.WBDataBase.Alias.Admin);
        }
        // 판매 정보 수정
        dao.update(namespace + "mergeSellInfo", paramDto.getSellInfo(), PartnerKey.WBDataBase.Alias.Admin);
        // 옵션 정보 수정
        dao.delete(namespace + "deleteOption", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin);
        Optional.ofNullable(paramDto.getOptionList()).orElseGet(Collections::emptyList)
                .forEach(option -> dao.insert(namespace + "insertOption", option, PartnerKey.WBDataBase.Alias.Admin));
        // 배송 정보
        dao.update(namespace + "mergeDelivery", paramDto.getDelivery(), PartnerKey.WBDataBase.Alias.Admin);
        // 정보 고시
        dao.update(namespace + "mergeInfoNotice", paramDto.getInfoNotice(), PartnerKey.WBDataBase.Alias.Admin);
        // 안내 정보
        dao.update(namespace + "mergeGuide", paramDto.getGuide(), PartnerKey.WBDataBase.Alias.Admin);


        // 상품 변경 이력
        changeInfoService.insertChangeInfo(paramDto.toChangeInfo());
        // 임시저장 파일 AWS S3 업로드
        fileService.s3FileUpload(paramDto.getFileList(), WBKey.Aws.A3.Product_Img_Path + paramDto.getProduct().getProductCode(), true);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    public void updateProductStatus(StatusUpdateDto paramDto) {
        // 이력정보 등록
        Arrays.stream(paramDto.getProductCodeList())
                .forEach(productCode -> {
                    String currentStatus = dao.selectOne(namespace + "findProductStatus", productCode, PartnerKey.WBDataBase.Alias.Admin);
                    // 상품 변경 이력
                    changeInfoService.insertChangeInfo(
                            paramDto.toChangeInfo(
                                    productCode,
                                    "DP".equals(paramDto.getStatusType()) ? currentStatus : paramDto.getStatusValue()
                            )
                    );

                    // 판매시작, 예약중 변경에 대한 재고 파악 체크
                    if (!"DP".equals(paramDto.getStatusType()) &&
                            (
                                ProductStatusCode.SALE.getCode().equals(paramDto.getStatusType())
                                ||
                                ProductStatusCode.RESERVATION.getCode().equals(paramDto.getStatusType())
                            )
                    ) {
                        // 판매 재고 0일 경우 예외처리 발생
                        if (dao.selectOne(namespace + "isZeroStock", productCode, PartnerKey.WBDataBase.Alias.Admin))
                            throw new WBBusinessException(ErrorCode.INVALID_NUMBER_MIN.getErrCode(), new String[]{"판매재고", "1"});
                    }
                });

        // 상태값 변경
        if ("DP".equals(paramDto.getStatusType())) {
            dao.update(namespace + "bulkUpdateProductDisplay", paramDto, PartnerKey.WBDataBase.Alias.Admin);
            return;
        }

        dao.update(namespace + "bulkUpdateProductStatus", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }

    public int findProductCountByPartnerCode(String partnerCode) {
        return dao.selectOne(namespace + "findProductCountByPartnerCode", partnerCode, PartnerKey.WBDataBase.Alias.Admin);
    }

    public void makeExcelFile(List<String> productCodeList,
                              HttpServletResponse response) throws IOException {
        // 엑셀 템플릿 사용하여 기본 설정
        ExcelUtil excel = new ExcelUtil(
                new FileInputStream(resourceLoader.getResource("classpath:templates/excel/productList.xlsx").getFile()),
                1
        );

        List<ProductExcelDto> productList = dao.selectList(namespace + "findExcelProductList", productCodeList, PartnerKey.WBDataBase.Alias.Admin);

        // 엑셀 시트 생성
        excel.sheet = excel.workbook.getSheetAt(0);

        // 엑셀 생성
        productList.forEach(product -> {
            int colIndex = 0;
            // 병합 사용 처리에 대한 카운트 처리
            ++excel.mergeCount;

            excel.row = excel.sheet.createRow(excel.rowNumber++);

            excel.setCellValue(colIndex++, product.getProductCode());
            excel.setCellValue(colIndex++, product.getBrandName());
            excel.setCellValue(colIndex++, product.getCategoryOneName());
            excel.setCellValue(colIndex++, product.getCategoryTwoName());
            excel.setCellValue(colIndex++, product.getCategoryThrName());
            excel.setCellValue(colIndex++, product.getProductName(), true);
            excel.setCellValue(colIndex++, product.getProductOption(), true);
            excel.setCellValue(colIndex++, product.getProductStockQty());
            excel.setCellValue(colIndex++, product.getFinalSellAmount());
            excel.setCellValue(colIndex++, product.getProductStatusCode());
            excel.setCellValue(colIndex++, product.getDisplayFlag());
            excel.setCellValue(colIndex++, product.getDeliveryType());
            excel.setCellValue(colIndex++, product.getDeliveryBundleFlag());
            excel.setCellValue(colIndex++, product.getProductSellStartDay());
            excel.setCellValue(colIndex++, product.getProductSellEndDay());
            excel.setCellValue(colIndex++, product.getCreateDay());
            excel.setCellValue(colIndex++, product.getUpdateDay());
            excel.setCellValue(colIndex, product.getCreateUserName(), true);

            // 셀 병합처리
            if (excel.mergeCount == product.getOptionCount()) {
                if (excel.mergeCount > 1)
                    for (int col = 0; col <= colIndex; col++) {
                        if (col < 6 | col > 8)
                            excel.sheet.addMergedRegion(new CellRangeAddress(excel.rowNumber - excel.mergeCount, excel.rowNumber - 1, col, col));
                    }

                excel.mergeCount = 0;
            }
        });

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode("상품목록리스트.xlsx", StandardCharsets.UTF_8) + "\";");
        excel.workbook.write(response.getOutputStream());
        excel.workbook.close();
    }
}
