package kr.wrightbrothers.apps.queue.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.ProductUtil;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.apps.queue.dto.ProductSendDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import kr.wrightbrothers.framework.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductQueueService {

    private final WBCommonDao dao;
    private final ProductService productService;
    private final ProductUtil productUtil;
    private final String namespace = "kr.wrightbrothers.apps.product.query.Product.";

    private String findCategoryName(String categoryCode) {
        return dao.selectOne("kr.wrightbrothers.apps.category.query.Category.findCategoryName", categoryCode, PartnerKey.WBDataBase.Alias.Admin);
    }

    private List<OptionDto.ReqBody> jsonToOptionList(JSONObject object) {
        if (ObjectUtils.isEmpty(object.getJSONArray("ProductOptin"))) return null;

        List<OptionDto.ReqBody> optionList = new ArrayList<>();
        JSONArray jsonOptionList = object.getJSONArray("ProductOptin");
        for (int i = 0; i < jsonOptionList.length(); i++) {
            optionList.add(
                    OptionDto.ReqBody.builder()
                            .productCode(object.getJSONObject("ProductMain").getString("ProductCode"))
                            .userId(object.getJSONObject("ProductMain").getString("UpdateUserId"))
                            .optionSeq(jsonOptionList.getJSONObject(i).getInt("OptionSequence"))
                            .optionName(jsonOptionList.getJSONObject(i).getString("OptionName"))
                            .optionValue(jsonOptionList.getJSONObject(i).getString("OptionValue"))
                            .optionSurcharge(jsonOptionList.getJSONObject(i).getLong("OptionSurcharge"))
                            .optionStockQty(jsonOptionList.getJSONObject(i).getInt("InventoryQuantity"))
                            .build());
        }

        return optionList;
    }

    // SNS 입점몰 상품 정보 조회
    public ProductSendDto findProductSnsData(String partnerCode,
                                             String productCode) {
        return ProductSendDto.builder()
                .partnerCode(partnerCode)
                .product(dao.selectOne(namespace + "findProduct", productCode))
                .basicSpec(dao.selectOne(namespace + "findBasicSpec", productCode))
                .sellInfo(dao.selectOne(namespace + "findSellInfo", productCode))
                .optionList(
                        dao.selectList(namespace + "findOptionList", productCode)
                                .stream()
                                .map(option -> {
                                    if (option instanceof OptionDto.ResBody)
                                        return OptionDto.Queue.builder()
                                                .optionSeq(((OptionDto.ResBody) option).getOptionSeq())
                                                .optionName(((OptionDto.ResBody) option).getOptionName())
                                                .optionValue(((OptionDto.ResBody) option).getOptionValue())
                                                .optionSurcharge(((OptionDto.ResBody) option).getOptionSurcharge())
                                                .optionStockQty(((OptionDto.ResBody) option).getOptionStockQty())
                                                .matadata(((OptionDto.ResBody) option).getOptionName() + "-" + ((OptionDto.ResBody) option).getOptionValue())
                                                .build();

                                    return null;
                                })
                                .collect(Collectors.toList())
                )
                .delivery(dao.selectOne(namespace + "findDelivery", productCode))
                .infoNotice(dao.selectOne(namespace + "findInfoNotice", productCode))
                .guide(dao.selectOne(namespace + "findGuide", productCode))
                .build();
    }

    /**
     * <pre>
     * 어드민 서비스에서 입점몰 상품 등록 처리일 경우 해당 상품 정보를 입점몰에 저장 합니다.
     * 저장 처리 경우에는 AOP Before 처리되는 SNS 데이터 전송은 예외처리 되었으니
     * 이부분 참고하시어 별도의 추가 구성은 하지 않아도 됩니다.
     * </pre>
     *
     * @param body SQS 수신 상품 등록 데이터
     */
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void insertProductSqsData(JSONObject body) {
        // Json -> ProductInsertDto
        ProductInsertDto paramDto = ProductInsertDto.builder()
                .product(ProductDto.ReqBody.jsonToProductDto(body))
                .basicSpec(BasicSpecDto.ReqBody.jsonToBasicSpecDto(body))
                .sellInfo(SellInfoDto.ReqBody.jsonToSellInfoDto(body))
                .optionList(jsonToOptionList(body))
                .delivery(DeliveryDto.ReqBody.jsonToDeliveryDto(body))
                .infoNotice(InfoNoticeDto.ReqBody.jsonToInfoNoticeDto(body))
                .guide(GuideDto.ReqBody.jsonToGuideDto(body))
                .build();

        // 카테고리 코드 -> 이름 데이터 셋팅
        paramDto.getProduct().setCategoryOneName(findCategoryName(paramDto.getProduct().getCategoryOneCode()));
        paramDto.getProduct().setCategoryTwoName(findCategoryName(paramDto.getProduct().getCategoryTwoCode()));
        paramDto.getProduct().setCategoryThrName(findCategoryName(paramDto.getProduct().getCategoryThrCode()));

        // SQS 입점몰 상품 등록
        productService.insertProduct(paramDto);
    }

    /**
     * <pre>
     * 어드민 서비스에서 입점몰 상품 변경 처리일 경우 해당 상품 정보를 입점몰에 갱신 합니다.
     * 수정 처리 경우에는 AOP Before 처리되는 SNS 데이터 전송은 예외처리 되었으니
     * 이부분 참고하시어 별도의 추가 구성은 하지 않아도 됩니다.
     * </pre>
     *
     * @param body SQS 수신 상품 수정 데이터
     */
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateProductSqsData(JSONObject body) {

        org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();

        ProductUpdateDto paramDto = ProductUpdateDto.builder()
                .productCode(body.getJSONObject("ProductMain").getString("ProductCode"))
                .product(ProductDto.ReqBody.jsonToProductDto(body))
                .basicSpec(BasicSpecDto.ReqBody.jsonToBasicSpecDto(body))
                .sellInfo(SellInfoDto.ReqBody.jsonToSellInfoDto(body))
                .optionList(jsonToOptionList(body))
                .delivery(DeliveryDto.ReqBody.jsonToDeliveryDto(body))
                .infoNotice(InfoNoticeDto.ReqBody.jsonToInfoNoticeDto(body))
                .guide(GuideDto.ReqBody.jsonToGuideDto(body))
                .build();

        // 카테고리 코드 -> 이름 데이터 셋팅
        paramDto.getProduct().setCategoryOneName(findCategoryName(paramDto.getProduct().getCategoryOneCode()));
        paramDto.getProduct().setCategoryTwoName(findCategoryName(paramDto.getProduct().getCategoryTwoCode()));
        paramDto.getProduct().setCategoryThrName(findCategoryName(paramDto.getProduct().getCategoryThrCode()));

        // 변경사항 로그 체크
        paramDto.setSqsLog(
                productUtil.productModifyCheck(
                        productService.findProduct(ProductFindDto.Param.builder()
                                .partnerCode(paramDto.getProduct().getPartnerCode())
                                .productCode(paramDto.getProductCode())
                                .build()),
                        paramDto.getProduct(),
                        paramDto.getBasicSpec(),
                        paramDto.getSellInfo(),
                        paramDto.getDelivery(),
                        paramDto.getInfoNotice(),
                        paramDto.getGuide()));

        // SQS 입점몰 상품 수정
        productService.updateProduct(paramDto);
    }

    /**
     * <pre>
     * 어드민 서비스에서 입점몰 상품 검수요청 결과에 대하 입점몰에 등록 합니다.
     * 검수관련 처리 경우에는 AOP Before SNS 데이터 전송은 예외처리 되었으니
     * 이부분 참고하시어 별도의 추가 구성은 하지 않아도 됩니다.
     * </pre>
     *
     * @param productUpdateDto 상품 입점몰 검수 결과 데이터
     */
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void updateInspectionSqsData(ProductUpdateDto productUpdateDto) {
        // SQS 입점몰 검수 결과
        productService.updateProduct(productUpdateDto);
    }

}
