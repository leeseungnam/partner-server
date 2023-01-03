package kr.wrightbrothers.apps.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.constants.ProductConst;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductUtil {

    private final WBCommonDao dao;
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
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
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
        } while ((boolean) dao.selectOne(namespace + "isProductCode", generateProductCode, PartnerKey.WBDataBase.Alias.Admin));

        return generateProductCode;
    }

    /**
     * 상품 정보의 수정여부에 대한 체크를 확인.
     *
     * @param findDto 현재 저장 상품 정보
     * @param product 변경체크 상품 정보
     * @param basicSpec 변경체크 기본스펙 정보
     * @param sellInfo 변경체크 판매 정보
     * @param delivery 변경체크 배송 정보
     * @param infoNotice 변경체크 고시 정보
     * @param guide 변경체크 안내 사항 정보
     * @return 변경 사항 내용
     */
    public String[] productModifyCheck(ProductFindDto.ResBody findDto,
                                       ProductDto.Product product,
                                       BasicSpecDto.BasicSpec basicSpec,
                                       SellInfoDto.SellInfo sellInfo,
                                       DeliveryDto.Delivery delivery,
                                       InfoNoticeDto.InfoNotice infoNotice,
                                       GuideDto.Guide guide) {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<String> logList = new ArrayList<>();

        if (!mapper.convertValue(findDto.getProduct(), ProductDto.Product.class).equals(mapper.convertValue(product, ProductDto.Product.class)))
            logList.add("상품 정보");

        if (!mapper.convertValue(findDto.getBasicSpec(), BasicSpecDto.BasicSpec.class).equals(mapper.convertValue(basicSpec, BasicSpecDto.BasicSpec.class)))
            logList.add("기본 스펙");

        if (!mapper.convertValue(findDto.getSellInfo(), SellInfoDto.SellInfo.class).equals(mapper.convertValue(sellInfo, SellInfoDto.SellInfo.class)))
            logList.add("판매 정보");

        if (!mapper.convertValue(findDto.getDelivery(), DeliveryDto.Delivery.class).equals(mapper.convertValue(delivery, DeliveryDto.Delivery.class)))
            logList.add("배송 정보");

        if (!mapper.convertValue(findDto.getInfoNotice(), InfoNoticeDto.InfoNotice.class).equals(mapper.convertValue(infoNotice, InfoNoticeDto.InfoNotice.class)))
            logList.add("상품 정보 고시");

        if (!findDto.getGuide().getProductDescription().equals(guide.getProductDescription()))
            logList.add("상세 설명");

        if (!findDto.getGuide().getProductGuide().equals(guide.getProductGuide()))
            logList.add("안내 사항");

        if (!findDto.getGuide().getDeliveryGuide().equals(guide.getDeliveryGuide()))
            logList.add("배송 안내");

        if (!findDto.getGuide().getExchangeReturnGuide().equals(guide.getExchangeReturnGuide()))
            logList.add("교환/반품 안내");

        if (!findDto.getGuide().getAsGuide().equals(guide.getAsGuide()))
            logList.add("A/S 안내");

        return logList.toArray(new String[0]);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateProductStatusStock(ProductUpdateDto paramDto) {
        String currentStatus = dao.selectOne(namespace + "findProductStatus", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin);
        // 판매완료 상태시 재고 0 이상일 경우 판매중 상태변경
        if (ProductConst.Status.SOLD_OUT.getCode().equals(currentStatus) && paramDto.getSellInfo().getProductStockQty() > 0)
            paramDto.getSellInfo().setProductStatusCode(ProductConst.Status.SALE.getCode());
        else if (!ProductConst.Status.SOLD_OUT.getCode().equals(currentStatus)
                && ProductConst.Status.SOLD_OUT.getCode().equals(paramDto.getSellInfo().getProductStatusCode())){
            // 상품 판매완료 상태는 기존 재구 무시하고 상품 재고 0개 처리(기획 장석민 협의 완료)
            paramDto.getSellInfo().setProductStockQty(0);
            if ("Y".equals(paramDto.getSellInfo().getProductOptionFlag()))
                paramDto.getOptionList().forEach(option -> option.setOptionStockQty(0));
        }
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateProductSellDate(String productCode,
                                      String changeStatusCode) {
        // 현재 상품 상태 코드
        String currentStatusCode = dao.selectOne(namespace + "findProductStatus", productCode, PartnerKey.WBDataBase.Alias.Admin);
        String nowDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

        // 상태 변경 아닐 시 종료
        if (currentStatusCode.equals(changeStatusCode)) return;

        switch (ProductConst.Status.of(changeStatusCode)) {
            case SALE:
                if (
                        // 검수 요청 대기
                        currentStatusCode.equals(ProductConst.Status.PRODUCT_INSPECTION.getCode())
                        ||
                        // 불가
                        currentStatusCode.equals(ProductConst.Status.REJECT_INSPECTION.getCode())
                )
                    dao.update(namespace + "updateProductSellDate",
                            SellInfoDto.ReqBody.builder()
                                    .productCode(productCode)
                                    .productSellStartDate(nowDate)
                                    .build(), PartnerKey.WBDataBase.Alias.Admin);

                log.info("Product Sale Start Date. Product Code::{}, Date::{}", productCode, nowDate);
                break;
            case END_OF_SALE:
                dao.update(namespace + "updateProductSellDate",
                        SellInfoDto.ReqBody.builder()
                                .productCode(productCode)
                                .productSellEndDate(nowDate)
                                .build(), PartnerKey.WBDataBase.Alias.Admin);

                log.info("Product Sale End Date. Product Code::{}, Date::{}", productCode, nowDate);
                break;
        }
    }

}
