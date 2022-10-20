package kr.wrightbrothers.apps.queue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.ProductUtil;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.apps.queue.dto.ProductReceiveDto;
import kr.wrightbrothers.apps.queue.dto.ProductSendDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
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
    public void insertProductSqsData(JSONObject body) throws JsonProcessingException {
        // 입점몰 상품 등록
        productService.insertProduct(convertProductDto(body));
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
    public void updateProductSqsData(JSONObject body) throws JsonProcessingException {
        // JSON -> ProductDTO 객체 변환
        ProductUpdateDto updateDto = convertProductDto(body);

        // 변경사항 로그 체크
        updateDto.setSqsLog(
                productUtil.productModifyCheck(
                        productService.findProduct(ProductFindDto.Param.builder()
                                .partnerCode(updateDto.getProduct().getPartnerCode())
                                .productCode(updateDto.getProductCode())
                                .build()),
                        updateDto.getProduct(),
                        updateDto.getBasicSpec(),
                        updateDto.getSellInfo(),
                        updateDto.getDelivery(),
                        updateDto.getInfoNotice(),
                        updateDto.getGuide()));

        // SQS 입점몰 상품 수정
        productService.updateProduct(updateDto);
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

    private ProductUpdateDto convertProductDto(JSONObject body) throws JsonProcessingException {
        ProductReceiveDto receiveDto = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .readValue(body.toString(), ProductReceiveDto.class);

        // 탑승자 연령대 세팅
        receiveDto.getBasicSpec().setAgeList(
                Optional.ofNullable(receiveDto.getAgeList()).orElse(Collections.emptyList())
                        .stream()
                        .map(ProductReceiveDto.Age::getAge)
                        .collect(Collectors.toList())
        );

        // 배송타입 세팅
        // 재생 자전거는 배송정보 없으므로 해당 부분 널 체크 확인
        if (!ObjectUtils.isEmpty(receiveDto.getDelivery()))
            receiveDto.getDelivery().setDeliveryType(
                    Optional.ofNullable(receiveDto.getDeliveryList()).orElse(Collections.emptyList())
                            .stream()
                            .map(ProductReceiveDto.DeliveryCode::getCode)
                            .findFirst()
                            .orElse(null)
            );

        // 객체 셋팅
        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .product(ProductDto.ReqBody.builder().build())
                .basicSpec(BasicSpecDto.ReqBody.builder().build())
                .sellInfo(SellInfoDto.ReqBody.builder().build())
                .delivery(DeliveryDto.ReqBody.builder().build())
                .infoNotice(InfoNoticeDto.ReqBody.builder().build())
                .guide(GuideDto.ReqBody.builder().build())
                .optionList(
                        receiveDto.getOptionList()
                                .stream()
                                .map(source -> {
                                    OptionDto.ReqBody target = OptionDto.ReqBody.builder().build();
                                    BeanUtils.copyProperties(source, target);
                                    return target;
                                })
                                .collect(Collectors.toList())
                )
                .build();
        BeanUtils.copyProperties(receiveDto.getProduct(), updateDto.getProduct());
        BeanUtils.copyProperties(receiveDto.getBasicSpec(), updateDto.getBasicSpec());
        BeanUtils.copyProperties(receiveDto.getSellInfo(), updateDto.getSellInfo());
        BeanUtils.copyProperties(receiveDto.getDelivery(), updateDto.getDelivery());
        BeanUtils.copyProperties(receiveDto.getInfoNotice(), updateDto.getInfoNotice());
        BeanUtils.copyProperties(receiveDto.getGuide(), updateDto.getGuide());

        // 상품 상세 설명 셋팅
        updateDto.getProduct().setProductDescription(receiveDto.getGuide().getProductDescription());

        // 사용자 아이디 셋팅
        updateDto.setAopUserId(receiveDto.getProduct().getUpdateUserId());
        updateDto.setSqsProductCode(updateDto.getProduct().getProductCode());

        // 카테고리 코드 -> 이름 데이터 셋팅
        updateDto.getProduct().setCategoryOneName(findCategoryName(updateDto.getProduct().getCategoryOneCode()));
        updateDto.getProduct().setCategoryTwoName(findCategoryName(updateDto.getProduct().getCategoryTwoCode()));
        updateDto.getProduct().setCategoryThrName(findCategoryName(updateDto.getProduct().getCategoryThrCode()));

        return updateDto;
    }

}
