package kr.wrightbrothers.apps.product.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.file.service.S3Service;
import kr.wrightbrothers.apps.product.dto.ProductInsertDto;
import kr.wrightbrothers.apps.product.dto.ProductListDto;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.product.query.Product.";
    private final FileService fileService;

    public String generateProductCode(String categoryTwoCode) {
        StringBuilder productCode = new StringBuilder();
        String productGroupCode = dao.selectOne(namespace + "findProductGroupCode", categoryTwoCode, PartnerKey.WBDataBase.Alias.Admin);
        // 파트너 신품 코드 영문 숫자조합 10자리
        // PA + 카테고리 상품 그룹코드 2자리 + FE + 영문/숫자 1자리 + 숫자 3
        productCode.append("PA");
        productCode.append(productGroupCode);
        productCode.append("FE");
        productCode.append(RandomStringUtils.randomAlphanumeric(1).toUpperCase());
        productCode.append(RandomStringUtils.randomNumeric(3));

        return productCode.toString();
    }

    public List<ProductListDto.Response> findProductList(ProductListDto.Param paramDto) {
        // 상품목록 조회
        return dao.selectList(namespace + "findProductList" ,paramDto);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void insertProduct(ProductInsertDto paramDto) {
        try {
            // 상품 기본정보 등록
            dao.insert(namespace + "insertProduct", paramDto.getProduct());
            // 상품 기본스펙
            if (ObjectUtils.isEmpty(paramDto.getBasicSpec())) {
                dao.insert(namespace + "insertBasicSpec", paramDto.getBasicSpec());
                // 연령 등록
                dao.insert(namespace + "insertBasicSpecAge", paramDto.getBasicSpec());
            }
            // 판매 정보
            dao.insert(namespace + "insertSellInfo", paramDto.getSellInfo());
            // 옵션 정보
            paramDto.getOptionList().forEach(option -> dao.insert(namespace + "insertOption", option));
            // 배송 정보
            dao.insert(namespace + "insertDelivery", paramDto.getDelivery());
            // 정보 고시
            dao.insert(namespace + "insertInfoNotice", paramDto.getInfoNotice());
            // 안내 정보
            dao.insert(namespace + "insertGuide", paramDto.getGuide());
            // 임시저장 파일 AWS S3 업로드
            fileService.s3FileUpload(paramDto.getFileList(), WBKey.Aws.A3.Product_Img_Path + paramDto.getProduct().getProductCode(), true);
        } catch (Exception e) {
            fileService.s3FileRollBack(WBKey.Aws.A3.Product_Img_Path + paramDto.getProduct().getProductCode());
            throw e;
        }
    }
}
