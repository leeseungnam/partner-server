package kr.wrightbrothers.apps.queue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.constants.Email;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.common.util.AwsSesUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.ProductUtil;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.apps.product.service.ChangeInfoService;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.apps.queue.dto.FindAddressDto;
import kr.wrightbrothers.apps.queue.dto.ProductReceiveDto;
import kr.wrightbrothers.apps.queue.dto.ProductSendDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQueueService {

    private final WBCommonDao dao;
    private final ProductService productService;
    private final ChangeInfoService changeInfoService;
    private final ProductUtil productUtil;
    private final AwsSesUtil awsSesUtil;
    private final String namespace = "kr.wrightbrothers.apps.queue.query.Queue.";

    private String findCategoryName(String categoryCode) {
        return dao.selectOne("kr.wrightbrothers.apps.category.query.Category.findCategoryName", categoryCode, PartnerKey.WBDataBase.Alias.Admin);
    }

    // SNS 입점몰 상품 정보 조회
    public ProductSendDto findProductSnsData(String partnerCode,
                                             String productCode) {
        String productNamespace = "kr.wrightbrothers.apps.product.query.Product.";
        return ProductSendDto.builder()
                .reqUserId(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .partnerCode(partnerCode)
                .product(dao.selectOne(productNamespace + "findProduct", productCode, PartnerKey.WBDataBase.Alias.Admin))
                .basicSpec(dao.selectOne(productNamespace + "findBasicSpec", productCode, PartnerKey.WBDataBase.Alias.Admin))
                .sellInfo(dao.selectOne(productNamespace + "findSellInfo", productCode, PartnerKey.WBDataBase.Alias.Admin))
                .optionList(
                        dao.selectList(productNamespace + "findOptionList", productCode, PartnerKey.WBDataBase.Alias.Admin)
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
                .delivery(dao.selectOne(productNamespace + "findDelivery", productCode, PartnerKey.WBDataBase.Alias.Admin))
                .infoNotice(dao.selectOne(productNamespace + "findInfoNotice", productCode, PartnerKey.WBDataBase.Alias.Admin))
                .guide(dao.selectOne(productNamespace + "findGuide", productCode, PartnerKey.WBDataBase.Alias.Admin))
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
        ProductInsertDto paramDto = convertProductDto(body);

        log.info("Product Insert From Admin. Partner Code::{}, Product Code::{}",
                paramDto.getProduct().getPartnerCode(), paramDto.getProduct().getProductCode());
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

        log.info("Product Update From Admin. Partner Code::{}, Product Code::{}, ChangeLog::{}",
                updateDto.getProduct().getPartnerCode(), updateDto.getProduct().getProductCode(), Arrays.toString(updateDto.getChangeLogList()));


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
        // SQS 검수 승인 상태가 없으므로 해당 상태코드는 판매 중으로 상태 변경.
        // Admin 검수 승인시 상품 이관 후 SQS 전송 하기에 데이터 정합성 문제 없음.
        if (ProductStatusCode.APPROVAL_INSPECTION.getCode().equals(productUpdateDto.getSellInfo().getProductStatusCode()))
            productUpdateDto.getSellInfo().setProductStatusCode(ProductStatusCode.SALE.getCode());

        log.info("Product Inspection Result From Admin. Partner Code::{}, Product Code::{}, ResultLog::{}",
                productUpdateDto.getProduct().getPartnerCode(), productUpdateDto.getProduct().getProductCode(), Arrays.toString(productUpdateDto.getChangeLogList()));

        // 판매 시작일 등록처리
        productUtil.updateProductSellDate(productUpdateDto.getProductCode(), productUpdateDto.getSellInfo().getProductStatusCode());
        // SQS 입점몰 검수 결과 처리
        dao.update(namespace + "updateProductStatus", productUpdateDto, PartnerKey.WBDataBase.Alias.Admin);
        changeInfoService.insertChangeInfo(productUpdateDto.toChangeInfo());

        // 상품 검수 요청 결과에 따른 메일 발송 처리
        Email email = Arrays.toString(productUpdateDto.getChangeLogList()).contains("검수 완료")
                ? Email.COMPLETE_PRODUCT : Email.REJECT_PRODUCT;

        FindAddressDto findAddressDto =
                dao.selectOne(namespace + "findAddressList", productUpdateDto.getProduct().getPartnerCode());

        // 메일 발송 대상자 없을 시 종료
        if (ObjectUtils.isEmpty(findAddressDto)) return;

        Context context = new Context();
        context.setVariable("partnerName", findAddressDto.getPartnerName());
        if (Email.REJECT_PRODUCT == email)
            context.setVariable("rejectMessages",
                    productUpdateDto.getChangeLogList()[0].substring(
                            productUpdateDto.getChangeLogList()[0].indexOf("(") + 1,
                            productUpdateDto.getChangeLogList()[0].length() - 1)
            );

        // 메일 발송 처리
        awsSesUtil.multiSend(
                email.getTitle(),
                email.getTemplate(),
                context,
                findAddressDto.getAddressList()
        );
    }

    private ProductUpdateDto convertProductDto(JSONObject body) throws JsonProcessingException {
        ProductReceiveDto receiveDto = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .readValue(body.toString(), ProductReceiveDto.class);

        // 탑승자 연령대 세팅
        if (!ObjectUtils.isEmpty(receiveDto.getBasicSpec()))
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
                .sellInfo(SellInfoDto.ReqBody.builder().build())
                .infoNotice(InfoNoticeDto.ReqBody.builder().build())
                .guide(GuideDto.ReqBody.builder().build())
                .optionList(
                        Optional.ofNullable(receiveDto.getOptionList()).orElse(Collections.emptyList())
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
        BeanUtils.copyProperties(receiveDto.getSellInfo(), updateDto.getSellInfo());
        BeanUtils.copyProperties(receiveDto.getInfoNotice(), updateDto.getInfoNotice());
        BeanUtils.copyProperties(receiveDto.getGuide(), updateDto.getGuide());

        // 기본 스펙
        if (!ObjectUtils.isEmpty(receiveDto.getBasicSpec())) {
            updateDto.setBasicSpec(BasicSpecDto.ReqBody.builder().build());
            BeanUtils.copyProperties(receiveDto.getBasicSpec(), updateDto.getBasicSpec());
        }

        // 배송 정보
        if (!ObjectUtils.isEmpty(receiveDto.getDelivery())) {
            updateDto.setDelivery(DeliveryDto.ReqBody.builder().build());
            BeanUtils.copyProperties(receiveDto.getDelivery(), updateDto.getDelivery());
        }

        // 상품 옵션 설정 여부
        updateDto.getSellInfo().setProductOptionFlag(
                updateDto.getOptionList().size() > 0 ? "Y" : "N"
        );

        // 상품 상세 설명 셋팅
        updateDto.getGuide().setProductDescription(receiveDto.getGuide().getProductDescription());

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
