package kr.wrightbrothers.apps.product.service;

import kr.wrightbrothers.apps.common.constants.ProductConst;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.ExcelUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey.WBDataBase.*;
import kr.wrightbrothers.apps.common.util.ProductUtil;
import kr.wrightbrothers.apps.file.dto.FileListDto;
import kr.wrightbrothers.apps.file.dto.FileUpdateDto;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<ProductListDto.Response> findProductList(ProductListDto.Param paramDto,
                                                         boolean isRowBounds) {
        return isRowBounds ? dao.selectList(namespace + "findProductList" ,paramDto, paramDto.getRowBounds(), Alias.Admin)
                .stream()
                .map(product -> (ProductListDto.Response) product)
                // 회원 정보는 RDS 나눠진 관계로 따로 회원명 처리
                .peek(product -> product.setCreateUserName(
                        dao.selectOne(namespace + "findProductCreateUser", product.getCreateUserId())
                ))
                .collect(Collectors.toList()) : dao.selectList(namespace + "findProductList" ,paramDto, Alias.Admin);
    }

    @Transactional(transactionManager = TransactionManager.Global)
    public void insertProduct(ProductInsertDto paramDto) {
        // 상품 데이터 저장
        dao.insert(namespace + "insertProduct", paramDto.getProduct(), Alias.Admin);
        dao.insert(namespace + "mergeSellInfo", paramDto.getSellInfo(), Alias.Admin);
        dao.insert(namespace + "mergeInfoNotice", paramDto.getInfoNotice(), Alias.Admin);
        dao.insert(namespace + "mergeGuide", paramDto.getGuide(), Alias.Admin);

        // 필수 데이터가 아닌 관계로 널체크 후 데이터 처리
        if ("Y".equals(paramDto.getSellInfo().getProductOptionFlag())) {
            Optional.ofNullable(paramDto.getOptionList()).orElseGet(Collections::emptyList)
                    .forEach(option -> dao.insert(namespace + "insertOption", option, Alias.Admin));
        }
        if (!ObjectUtils.isEmpty(paramDto.getDelivery())) {
            dao.insert(namespace + "insertDelivery", paramDto.getDelivery(), Alias.Admin);
        }
        if (!ObjectUtils.isEmpty(paramDto.getBasicSpec())) {
            dao.insert(namespace + "mergeBasicSpec", paramDto.getBasicSpec(), Alias.Admin);
            if (!ObjectUtils.isEmpty(paramDto.getBasicSpec().getAgeList())) {
                dao.insert(namespace + "insertBasicSpecAge", paramDto.getBasicSpec(), Alias.Admin);
            }
        }

        // 이력 및 S3 처리
        changeInfoService.insertChangeInfo(paramDto.toChangeInfo());
        fileService.s3FileUpload(paramDto.getFileList(), WBKey.Aws.A3.Product_Img_Path + paramDto.getProduct().getProductCode(), true);
    }

    public ProductFindDto.ResBody findProduct(ProductFindDto.Param paramDto) {
        List<OptionDto.ResBody> optionList = dao.selectList(namespace + "findOptionList", paramDto.getProductCode(), Alias.Admin);
        ProductFindDto.ResBody findDto = ProductFindDto.ResBody.builder()
                .product(dao.selectOne(namespace + "findProduct", paramDto.getProductCode(), Alias.Admin))
                .basicSpec(dao.selectOne(namespace + "findBasicSpec", paramDto.getProductCode(), Alias.Admin))
                .sellInfo(dao.selectOne(namespace + "findSellInfo", paramDto.getProductCode(), Alias.Admin))
                .optionList(Optional.ofNullable(optionList).orElseGet(Collections::emptyList))
                .delivery(dao.selectOne(namespace + "findDelivery", paramDto.getProductCode(), Alias.Admin))
                .infoNotice(dao.selectOne(namespace + "findInfoNotice", paramDto.getProductCode(), Alias.Admin))
                .guide(dao.selectOne(namespace + "findGuide", paramDto.getProductCode(), Alias.Admin))
                .build();

        if (Objects.requireNonNull(optionList).size() == 1 && optionList.get(0).getOptionName().contains("임시"))
            optionList.clear();

        if (!ObjectUtils.isEmpty(findDto.getOptionList()) && "N".equals(findDto.getSellInfo().getProductOptionFlag()))
            findDto.getSellInfo().setProductOptionFlag("Y");

        if (ProductConst.Status.REJECT_INSPECTION.getCode().equals(findDto.getSellInfo().getProductStatusCode()))
            findDto.setRejectReason(dao.selectOne(namespace + "findProductRejectReason", paramDto.getProductCode()));

        return findDto;
    }

    @Transactional(transactionManager = TransactionManager.Global)
    public void productChangeLog(ProductUpdateDto paramDto) {
        ProductFindDto.ResBody currentProduct = findProduct(paramDto.toProductFindParam());
        paramDto.setSqsLog(
                productUtil.productModifyCheck(
                        currentProduct,
                        paramDto.getProduct(),
                        paramDto.getBasicSpec(),
                        paramDto.getSellInfo(),
                        paramDto.getDelivery(),
                        paramDto.getInfoNotice(),
                        paramDto.getGuide()
                ));

        List<FileListDto> currentFileList = fileService.findFileList(paramDto.getProduct().getProductFileNo());

        if (paramDto.getFileList().size() != currentFileList.size())
            paramDto.setSqsLog(ArrayUtils.add(paramDto.getChangeLogList(), "상품 이미지"));
        else {
            StringBuilder currentFileStr = new StringBuilder();
            for (FileListDto dto : currentFileList)
                currentFileStr.append(dto.getFileOriginalName()).append(dto.getFileStatus());
            StringBuilder updateFileStr = new StringBuilder();
            for (FileUpdateDto dto : paramDto.getFileList())
                updateFileStr.append(dto.getFileOriginalName()).append(dto.getFileStatus());
            if (!currentFileStr.toString().equals(updateFileStr.toString()))
                paramDto.setSqsLog(ArrayUtils.add(paramDto.getChangeLogList(), "상품 이미지"));
        }

        if (ObjectUtils.isEmpty(currentProduct.getOptionList()) & ObjectUtils.isEmpty(paramDto.getOptionList())) return;


        StringBuilder currentOptionStr = new StringBuilder();
        if (!ObjectUtils.isEmpty(currentProduct.getOptionList()))
            for (OptionDto.ResBody dto : currentProduct.getOptionList())
                currentOptionStr.append(dto.getOptionName()).append(dto.getOptionValue()).append(dto.getOptionSurcharge()).append(dto.getOptionStockQty());
        StringBuilder updateOptionStr = new StringBuilder();
        if (!ObjectUtils.isEmpty(paramDto.getOptionList()))
            for (OptionDto.ReqBody dto : paramDto.getOptionList())
                updateOptionStr.append(dto.getOptionName()).append(dto.getOptionValue()).append(dto.getOptionSurcharge()).append(dto.getOptionStockQty());
        if (!currentOptionStr.toString().equals(updateOptionStr.toString()))
            paramDto.setSqsLog(ArrayUtils.add(paramDto.getChangeLogList(), "옵션 정보"));

    }

    @Transactional(transactionManager = TransactionManager.Global)
    public void updateProduct(ProductUpdateDto paramDto) {
        // 업데이트 처리 전 변경로그, 재고에 따른 상태처리, 판매일자 처리
        productChangeLog(paramDto);
        productUtil.updateProductStatusStock(paramDto);
        productUtil.updateProductSellDate(paramDto.getProductCode(), paramDto.getSellInfo().getProductStatusCode());

        // 판매종료 시 노출 N 변경
        if (ProductConst.Status.END_OF_SALE.getCode().equals(paramDto.getSellInfo().getProductStatusCode()))
            paramDto.getSellInfo().setDisplayFlag("N");

        // 상품 데이터 수정
        dao.update(namespace + "updateProduct", paramDto.getProduct(), Alias.Admin);
        dao.update(namespace + "mergeSellInfo", paramDto.getSellInfo(), Alias.Admin);
        dao.update(namespace + "mergeInfoNotice", paramDto.getInfoNotice(), Alias.Admin);
        dao.update(namespace + "mergeGuide", paramDto.getGuide(), Alias.Admin);
        dao.delete(namespace + "deleteOption", paramDto.getProductCode(), Alias.Admin);

        // 필수 데이터가 아닌 관계로 널체크 후 데이터 처리
        if ("Y".equals(paramDto.getSellInfo().getProductOptionFlag())) {
            Optional.ofNullable(paramDto.getOptionList()).orElseGet(Collections::emptyList)
                    .forEach(option -> dao.insert(namespace + "insertOption", option, Alias.Admin));
        }
        if (!ObjectUtils.isEmpty(paramDto.getBasicSpec())) {
            dao.update(namespace + "mergeBasicSpec", paramDto.getBasicSpec(), Alias.Admin);
            dao.delete(namespace + "deleteBasicSpecAge", paramDto.getProductCode(), Alias.Admin);
            if (!ObjectUtils.isEmpty(paramDto.getBasicSpec().getAgeList())) {
                dao.insert(namespace + "insertBasicSpecAge", paramDto.getBasicSpec(), Alias.Admin);
            }
        }
        if (!ObjectUtils.isEmpty(paramDto.getDelivery())) {
            dao.update(namespace + "updateDelivery", paramDto.getDelivery(), Alias.Admin);
        }

        // 이력 및 S3 처리
        changeInfoService.insertChangeInfo(paramDto.toChangeInfo());
        fileService.s3FileUpload(paramDto.getFileList(), WBKey.Aws.A3.Product_Img_Path + paramDto.getProduct().getProductCode(), true);
    }

    @Transactional(transactionManager = TransactionManager.Global)
    public void updateProductStatus(StatusUpdateDto paramDto) {
        // 이력정보 등록
        Arrays.stream(paramDto.getProductCodeList())
                .forEach(productCode -> {
                    String currentStatus = dao.selectOne(namespace + "findProductStatus", productCode, Alias.Admin);
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
                                ProductConst.Status.SALE.getCode().equals(paramDto.getStatusValue())
                                ||
                                ProductConst.Status.RESERVATION.getCode().equals(paramDto.getStatusValue())
                            )
                    ) {
                        // 판매 재고 0일 경우 예외처리 발생
                        if (dao.selectOne(namespace + "isZeroStock", productCode, Alias.Admin))
                            throw new WBBusinessException(ErrorCode.INVALID_NUMBER_MIN.getErrCode(), new String[]{"판매재고", "1"});
                    }

                    if (!"DP".equals(paramDto.getStatusType()) && currentStatus.equals(paramDto.getStatusValue())) {
                        switch (ProductConst.Status.of(paramDto.getStatusValue())) {
                            case SALE:
                                throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_STATUS.getErrCode(), new String[]{"예약중/판매완료"});
                            case END_OF_SALE:
                            case RESERVATION:
                                throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_STATUS.getErrCode(), new String[]{"판매중"});
                        }
                    }
                });

        // 상태값 변경
        if ("DP".equals(paramDto.getStatusType())) {
            dao.update(namespace + "bulkUpdateProductDisplay", paramDto, Alias.Admin);
            return;
        }

        dao.update(namespace + "bulkUpdateProductStatus", paramDto, Alias.Admin);
    }

    public int findProductCountByPartnerCode(String partnerCode) {
        return dao.selectOne(namespace + "findProductCountByPartnerCode", partnerCode, Alias.Admin);
    }

    public void makeExcelFile(List<String> productCodeList,
                              HttpServletResponse response) throws IOException {
        // 엑셀 템플릿 사용하여 기본 설정
        ExcelUtil excel = new ExcelUtil(
                resourceLoader.getResource("classpath:templates/excel/productList.xlsx").getInputStream(),
                1
        );

        if (ObjectUtils.isEmpty(productCodeList)) {
            excel.excelWrite("상품목록리스트.xlsx", response);
            return;
        }

        List<ProductExcelDto> productList = dao.selectList(namespace + "findExcelProductList", productCodeList, Alias.Admin);

        // 엑셀 생성
        productList.forEach(product -> {
            int colIndex = 18;
            // 병합 사용 처리에 대한 카운트 처리
            ++excel.mergeCount;

            excel.row = excel.sheet.createRow(excel.rowNumber++);
            // 엑셀 생성 처리
            excel.setCellValue(product);

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

        excel.excelWrite("상품목록리스트.xlsx", response);
    }

    @Transactional(transactionManager = TransactionManager.Global)
    public void deleteProduct(ProductDeleteDto paramDto) {
        if (dao.selectOne(namespace + "isNonInspectionReject", paramDto, Alias.Admin))
            throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_DELETE.getErrCode());

        // 주문상품 삭제
        Arrays.stream(paramDto.getProductCodeList()).forEach(productCode -> {
            // 상품테이블 삭제
            dao.delete(namespace + "deleteSellInfo", productCode, Alias.Admin);
            dao.delete(namespace + "deleteInfoNotice", productCode, Alias.Admin);
            dao.delete(namespace + "deleteGuide", productCode, Alias.Admin);
            dao.delete(namespace + "deleteBasicSpec", productCode, Alias.Admin);
            dao.delete(namespace + "deleteDelivery", productCode, Alias.Admin);
            dao.delete(namespace + "deleteOption", productCode, Alias.Admin);
            dao.delete(namespace + "deleteProduct", productCode, Alias.Admin);

            // 검수테이블 삭제
            dao.delete(namespace + "deleteProductRequest", productCode, Alias.Admin);
            dao.delete(namespace + "deleteProductRequestHistory", productCode, Alias.Admin);
        });
    }
}
