package kr.wrightbrothers.apps.product.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;

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
    private final FileService fileService;
    private final ChangeInfoService changeInfoService;

    /**
     * <pre>
     *     상품코드 자리수 11 자리
     *     상품 코드 생성 조합 규칙
     *
     *     파트너 구분 코드 2자리 : PA
     *     카테고리 그룹 코드 2자리
     *     정산 구분 코드 2자리 : FE
     *     숫자 + 알파벳 5자리 : A0B0H
     *
     *     예: PABFFEA0B0H
     * </pre>
     */
    public String generateProductCode(String categoryTwoCode) {
        StringBuilder productCode = new StringBuilder();
        String productGroupCode = dao.selectOne(namespace + "findProductGroupCode", categoryTwoCode, PartnerKey.WBDataBase.Alias.Admin);
        // 파트너 신품 코드 영문 숫자조합 11자리
        // PA + 카테고리 상품 그룹코드 2자리 + FE + 영문/숫자 5자리
        productCode.append("PA");
        productCode.append(productGroupCode);
        productCode.append("FE");
        productCode.append(RandomStringUtils.randomAlphanumeric(5).toUpperCase());

        return productCode.toString();
    }

    public List<ProductListDto.Response> findProductList(ProductListDto.Param paramDto) {
        // 상품목록 조회
        return dao.selectList(namespace + "findProductList" ,paramDto, paramDto.getRowBounds());
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void insertProduct(ProductInsertDto paramDto) {
        // 상품 기본정보 등록
        dao.insert(namespace + "insertProduct", paramDto.getProduct());
        // 상품 기본스펙
        if (!ObjectUtils.isEmpty(paramDto.getBasicSpec())) {
            dao.insert(namespace + "mergeBasicSpec", paramDto.getBasicSpec());
            // 연령 등록
            dao.insert(namespace + "insertBasicSpecAge", paramDto.getBasicSpec());
        }
        // 판매 정보
        dao.insert(namespace + "mergeSellInfo", paramDto.getSellInfo());
        // 옵션 정보
        paramDto.getOptionList().forEach(option -> dao.insert(namespace + "insertOption", option));
        // 배송 정보
        dao.insert(namespace + "mergeDelivery", paramDto.getDelivery());
        // 정보 고시
        dao.insert(namespace + "mergeInfoNotice", paramDto.getInfoNotice());
        // 안내 정보
        dao.insert(namespace + "mergeGuide", paramDto.getGuide());
        // 변경 이력 등록
        changeInfoService.insertChangeInfo(paramDto.toChangeInfo());
        // 임시저장 파일 AWS S3 업로드
        fileService.s3FileUpload(paramDto.getFileList(), WBKey.Aws.A3.Product_Img_Path + paramDto.getProduct().getProductCode(), true);
    }

    public ProductFindDto.ResBody findProduct(ProductFindDto.Param paramDto) {
        return ProductFindDto.ResBody.builder()
                .product(dao.selectOne(namespace + "findProduct", paramDto.getProductCode()))
                .basicSpec(dao.selectOne(namespace + "findBasicSpec", paramDto.getProductCode()))
                .sellInfo(dao.selectOne(namespace + "findSellInfo", paramDto.getProductCode()))
                .optionList(dao.selectList(namespace + "findOptionList", paramDto.getProductCode()))
                .delivery(dao.selectOne(namespace + "findDelivery", paramDto.getProductCode()))
                .infoNotice(dao.selectOne(namespace + "findInfoNotice", paramDto.getProductCode()))
                .guide(dao.selectOne(namespace + "findGuide", paramDto.getProductCode()))
                .build();
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateProduct(ProductUpdateDto paramDto) {
        // 상품 기본정보 수정
        dao.update(namespace + "updateProduct", paramDto.getProduct());
        // 상품 기본스펙 수정
        if (!ObjectUtils.isEmpty(paramDto.getBasicSpec())) {
            dao.update(namespace + "mergeBasicSpec", paramDto.getBasicSpec());
            // 연령 삭제
            dao.delete(namespace + "deleteBasicSpecAge", paramDto.getProductCode());
            // 연령 등록
            dao.insert(namespace + "insertBasicSpecAge", paramDto.getBasicSpec());
        }
        // 판매 정보 수정
        dao.update(namespace + "mergeSellInfo", paramDto.getSellInfo());
        // 옵션 정보 수정
        dao.delete(namespace + "deleteOption", paramDto.getProductCode());
        paramDto.getOptionList().forEach(option -> dao.insert(namespace + "insertOption", option));
        // 배송 정보
        dao.update(namespace + "mergeDelivery", paramDto.getDelivery());
        // 정보 고시
        dao.update(namespace + "mergeInfoNotice", paramDto.getInfoNotice());
        // 안내 정보
        dao.update(namespace + "mergeGuide", paramDto.getGuide());
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
                    String currentStatus = dao.selectOne(namespace + "findProductStatus", productCode);
                    // 상품 변경 이력
                    changeInfoService.insertChangeInfo(
                            paramDto.toChangeInfo(
                                    productCode,
                                    "DP".equals(paramDto.getStatusType()) ? currentStatus : paramDto.getStatusValue()
                            )
                    );
                });

        // 상태값 변경
        if ("DP".equals(paramDto.getStatusType())) {
            dao.update(namespace + "bulkUpdateProductDisplay", paramDto);
            return;
        }

        dao.update(namespace + "bulkUpdateProductStatus", paramDto);
    }

}
