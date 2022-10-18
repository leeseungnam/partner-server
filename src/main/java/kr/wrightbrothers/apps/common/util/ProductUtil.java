package kr.wrightbrothers.apps.common.util;

import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductUtil {

    private final WBCommonDao dao;
    private final ProductService productService;
    private final String namespace = "kr.wrightbrothers.apps.product.query.Product.";

    /**
     * <pre>
     * 상품코드 자리수 10 자리
     * 상품 코드 생성 조합 규칙
     *
     * 파트너 구분 코드 2자리 : PA
     * 카테고리 그룹 코드 2자리
     * 숫자 + 알파벳 6자리 : A0B0EH
     *
     * 예: PABFA0B0HZ
     * </pre>
     */
    public String generateProductCode(String categoryTwoCode) {
        StringBuilder productCode = new StringBuilder();
        String productGroupCode = dao.selectOne(namespace + "findProductGroupCode", categoryTwoCode, PartnerKey.WBDataBase.Alias.Admin);
        // 파트너 신품 코드 영문 숫자조합 10자리
        // PA + 카테고리 상품 그룹코드 2자리 + 영문/숫자 6자리
        productCode.append("PA");
        productCode.append(productGroupCode);

        // 등록여부 확인 후 코드 생성 처리
        String generateProductCode;
        do {
            generateProductCode = productCode + RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        } while (dao.selectOne(namespace + "isProductCode", generateProductCode));

        return generateProductCode;
    }

    /**
     * 상품 정보의 수정여부에 대한 체크를 확인.
     *
     * @param paramDto 조회 파라미터
     * @param product 변경체크 상품 정보
     * @param basicSpec 변경체크 기본스펙 정보
     * @param sellInfo 변경체크 판매 정보
     * @param delivery 변경체크 배송 정보
     * @param infoNotice 변경체크 고시 정보
     * @param guide 변경체크 안내 사항 정보
     * @return 변경 사항 내용
     */
    public String[] productModifyCheck(ProductFindDto.Param paramDto,
                                       ProductDto.Product product,
                                       BasicSpecDto.BasicSpec basicSpec,
                                       SellInfoDto.SellInfo sellInfo,
                                       DeliveryDto.Delivery delivery,
                                       InfoNoticeDto.InfoNotice infoNotice,
                                       GuideDto.Guide guide) {
        List<String> logList = new ArrayList<>();
        // 현재 상품 정보 조회
        ProductFindDto.ResBody findDto = productService.findProduct(paramDto);

        if (!findDto.getProduct().equals(product))
            logList.add("상품 정보");

        if (!ObjectUtils.isEmpty(findDto.getBasicSpec()) && !findDto.getBasicSpec().equals(basicSpec))
            logList.add("기본 스펙");

        if (!findDto.getSellInfo().equals(sellInfo))
            logList.add("판매 정보");

        if (!ObjectUtils.isEmpty(findDto.getDelivery()) && !findDto.getDelivery().equals(delivery))
            logList.add("배송 정보");

        if (!findDto.getInfoNotice().equals(infoNotice))
            logList.add("상품 정보 고시");

        if (!findDto.getGuide().equals(guide))
            logList.add("안내 사항");

        return logList.toArray(new String[0]);
    }

}
