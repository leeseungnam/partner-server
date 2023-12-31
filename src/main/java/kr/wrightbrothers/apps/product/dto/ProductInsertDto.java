package kr.wrightbrothers.apps.product.dto;

import kr.wrightbrothers.apps.common.constants.ProductConst;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.file.dto.FileUpdateDto;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInsertDto {

    /** 상품 정보 */
    @Valid @NotNull(message = "상품 정보")
    private ProductDto.ReqBody product;

    /** 기본 스펙 정보 */
    private BasicSpecDto.ReqBody basicSpec;

    /** 판매 정보 */
    @Valid @NotNull(message = "판매 정보")
    private SellInfoDto.ReqBody sellInfo;

    /** 옵션 정보 */
    @Valid
    private List<OptionDto.ReqBody> optionList;

    /** 배송 정보 */
    private DeliveryDto.ReqBody delivery;

    /** 상품 정보 고시 */
    @Valid @NotNull(message = "상품 정보 고시")
    private InfoNoticeDto.ReqBody infoNotice;

    /** 안내 사항 */
    @Valid @NotNull(message = "안내 사항 정보")
    private GuideDto.ReqBody guide;

    /** 상품 등록 이미지 */
    @NotNull(message = "상품 이미지 파일 목록")
    private List<FileUpdateDto> fileList;

    public void setBasicSpec(BasicSpecDto.ReqBody paramDto) {
        this.basicSpec = paramDto;
    }
    public void setDelivery(DeliveryDto.ReqBody paramDto) {
        this.delivery = paramDto;
    }

    // 자전거 상품 추가 유효성 검사
    private void validBike() {
        // 자전거 상품이 아닐경우 제외
        if (!ProductConst.Category.BIKE.getCode().equals(this.product.getCategoryOneCode())) return;

        // 브랜드 압력요청 처리
        if (ObjectUtils.isEmpty(this.product.getBrandNo()) && ObjectUtils.isEmpty(this.product.getBrandName()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"브랜드명"});

        // 모델 입력요청 처리
        if (ObjectUtils.isEmpty(this.product.getModelCode()) && ObjectUtils.isEmpty(this.product.getModelName()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"모델명"});

        if (ObjectUtils.isEmpty(this.product.getBrandNo()))
            this.product.setBrandNo("0");
        if (ObjectUtils.isEmpty(this.product.getModelCode()))
            this.product.setModelCode("0");

    }

    // 상품 옵션 유효성 검사
    private void validOption() {
        if ("Y".equals(this.sellInfo.getProductOptionFlag())) {
            if (ObjectUtils.isEmpty(this.optionList))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"옵션 정보"});

            // 재고
            int productStockQty = 0;
            List<String> optionCheckList = new ArrayList<>();
            String preOptionName = "";
            String preOptionValue = "";
            for (OptionDto.ReqBody option : this.optionList) {
                // 옵션 중복등록 유효성 체크
                if (option.getOptionName().equals(preOptionName) && option.getOptionValue().equals(preOptionValue))
                    throw new WBBusinessException(ErrorCode.DUPLICATION_OBJECT.getErrCode(), new String[]{"옵션 정보 "});

                if (ObjectUtils.isEmpty(option.getOptionSeq()))
                    throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"옵션 번호"});
                if (ObjectUtils.isEmpty(option.getOptionName()))
                    throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"옵션 명"});
                if (ObjectUtils.isEmpty(option.getOptionValue()))
                    throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"옵션 항목"});
                if (ObjectUtils.isEmpty(option.getOptionSurcharge()))
                    throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"변동 금액"});
                if (ObjectUtils.isEmpty(option.getOptionStockQty()))
                    throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"옵션 재고수량"});

                if (option.getOptionName().length() > 20)
                    throw new WBBusinessException(ErrorCode.INVALID_TEXT_SIZE.getErrCode(), new String[]{"옵션명", "1", "20"});
                if (option.getOptionValue().length() > 20)
                    throw new WBBusinessException(ErrorCode.INVALID_TEXT_SIZE.getErrCode(), new String[]{"옵션항목", "1", "20"});
                if (option.getOptionSurcharge() > 10000000)
                    throw new WBBusinessException(ErrorCode.INVALID_MONEY_MAX.getErrCode(), new String[]{"변동금액", "10,000,000"});
                if (option.getOptionSurcharge() < -10000000)
                    throw new WBBusinessException(ErrorCode.INVALID_MONEY_MIN.getErrCode(), new String[]{"변동금액", "-10,000,000"});
                if (option.getOptionStockQty() > 9999)
                    throw new WBBusinessException(ErrorCode.INVALID_NUMBER_MAX.getErrCode(), new String[]{"옵션 재고수량", "9,999"});

                productStockQty += option.getOptionStockQty();
                preOptionName = option.getOptionName();
                preOptionValue = option.getOptionValue();
                optionCheckList.add(option.getOptionName() + option.getOptionValue());
            }
            // 재고 수량 유효성 확인
            if (this.sellInfo.getProductStockQty() != productStockQty)
                throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_STOCK.getErrCode(), new String[]{""});

             if (this.optionList.size() != optionCheckList.stream().distinct().count())
                 throw new WBBusinessException(ErrorCode.DUPLICATION_OBJECT.getErrCode(), new String[]{"옵션 정보 "});
        }
    }

    public void validProduct() {
        // 상품이미지
        if (ObjectUtils.isEmpty(fileList))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"상품 이미지"});

        if (ObjectUtils.isEmpty(this.product.getBrandNo()))
            this.product.setBrandNo("0");

        // 자전거 상품 추가 유효성 검사
        validBike();

        // 판매재고 최대 수량 체크
        if (this.sellInfo.getProductStockQty() > 9999)
            throw new WBBusinessException(ErrorCode.INVALID_NUMBER_MAX.getErrCode(), new String[]{"재고", "9,999"});

        // 이미지 30장 이상 등록 유효성 검사
        AtomicInteger imgCount = new AtomicInteger();
        fileList.forEach(file -> {
            if ("D".equals(file.getFileStatus())) return;
            imgCount.getAndIncrement();
        });

        if (imgCount.get() > 30)
            throw new WBBusinessException(ErrorCode.INVALID_IMAGE_MAX.getErrCode(), new String[]{"30개"});

        // 자전거 기본스펙 해당 필드 Null 처리
        if (!ObjectUtils.isEmpty(this.basicSpec)) {
            if ("".equals(this.basicSpec.getMaxHeightPerson()) | "0".equals(this.basicSpec.getMaxHeightPerson()))
                this.basicSpec.setMaxHeightPerson(null);
            if ("".equals(this.basicSpec.getMinHeightPerson()) | "0".equals(this.basicSpec.getMinHeightPerson()))
                this.basicSpec.setMinHeightPerson(null);
            if ("".equals(this.basicSpec.getBikeWeight()) | "0".equals(this.basicSpec.getBikeWeight()))
                this.basicSpec.setBikeWeight(null);

            // 호환키 유효성
            if (ObjectUtils.isEmpty(this.basicSpec.getMinHeightPerson()) || ObjectUtils.isEmpty(this.basicSpec.getMaxHeightPerson())) {
                if (!(ObjectUtils.isEmpty(this.basicSpec.getMinHeightPerson()) && ObjectUtils.isEmpty(this.basicSpec.getMaxHeightPerson())))
                    throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"호환키"});
            }

            // 기본스펙 호환키에 대한 유효성 체크
            if (!ObjectUtils.isEmpty(this.basicSpec.getMinHeightPerson()) && !ObjectUtils.isEmpty(this.basicSpec.getMaxHeightPerson())) {
                if (Long.parseLong(this.basicSpec.getMinHeightPerson()) > Long.parseLong(this.basicSpec.getMaxHeightPerson()))
                    throw new WBBusinessException(ErrorCode.INVALID_RANGE.getErrCode(), new String[]{"호환키"});
                if (Long.parseLong(this.basicSpec.getMinHeightPerson()) < 80)
                    throw new WBBusinessException(ErrorCode.INVALID_NUMBER_MIN.getErrCode(), new String[]{"최소키", "80"});
                if (Long.parseLong(this.basicSpec.getMaxHeightPerson()) > 250)
                    throw new WBBusinessException(ErrorCode.INVALID_NUMBER_MAX.getErrCode(), new String[]{"최대키", "250"});
            }

        }

        if (ObjectUtils.isEmpty(this.sellInfo.getProductStatusCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"상품 진행 상태"});

        // 예약중 상태일 경우 판매재고 0 이상의 유효성 체크
        if (ProductConst.Status.RESERVATION.getCode().equals(this.sellInfo.getProductStatusCode())) {
            if (this.sellInfo.getProductStockQty() == 0)
                throw new WBBusinessException(ErrorCode.INVALID_NUMBER_MIN.getErrCode(), new String[]{"판매재고", "1"});
        }

        // 판매중 상태에서의 재고 0일경우 판매완료 상태변경
        if (ProductConst.Status.SALE.getCode().equals(this.sellInfo.getProductStatusCode()) && this.sellInfo.getProductStockQty() == 0)
            this.sellInfo.setProductStatusCode(ProductConst.Status.SOLD_OUT.getCode());

        // 상품 판매 옵션 유효성 검사
        validOption();
        // 재생자전거 유효성 검사 제외
        if (ProductConst.Type.RECYCLING.getType().equals(this.product.getProductType())) {
            if (ObjectUtils.isEmpty(this.guide.getQnaGuide()))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"자주 묻는 질문"});
            if (this.guide.getQnaGuide().length() < 30)
                throw new WBBusinessException(ErrorCode.INVALID_TEXT_SIZE.getErrCode(), new String[]{"자주 묻는 질문", "30", "2000"});
            return;
        }

        if (ObjectUtils.isEmpty(this.guide.getExchangeReturnGuide()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"교환/반품 안내"});
        if (this.guide.getExchangeReturnGuide().length() < 30)
            throw new WBBusinessException(ErrorCode.INVALID_TEXT_SIZE.getErrCode(), new String[]{"교환/반품 안내", "30", "2000"});
        // 배송정보 유효성 검사
        this.delivery.validDelivery();
    }

    public void setAopUserId(String userId) {
        // 필수 데이터 입력 부분
            this.product.setUserId(userId);
            this.sellInfo.setUserId(userId);
            this.infoNotice.setUserId(userId);
            this.guide.setUserId(userId);

        if (!ObjectUtils.isEmpty(this.optionList) && ObjectUtils.isEmpty(this.optionList.get(0).getOptionName()))
            this.optionList = null;

        if (!ObjectUtils.isEmpty(this.optionList))
            Optional.ofNullable(this.optionList).orElseGet(Collections::emptyList)
                    .forEach(option -> option.setUserId(userId));
        if (!ObjectUtils.isEmpty(this.fileList))
            Optional.ofNullable(this.fileList).orElseGet(Collections::emptyList)
                    .forEach(file -> file.setUserId(userId));
        if (!ObjectUtils.isEmpty(this.delivery))
            this.delivery.setUserId(userId);
        if (!ObjectUtils.isEmpty(this.basicSpec))
            this.basicSpec.setUserId(userId);
    }

    public void setAopPartnerCode(String partnerCode) {
        // 필수 데이터 입력 부분
        this.product.setPartnerCode(partnerCode);
    }

    public void setProductType(String partnerKind) {
        // 재생입점 재생자전거 코드 설정
        if ("2".equals(partnerKind)) {
            this.product.setProductType("P04");     // 재생
            this.delivery.setDeliveryType("D05");   // 재생배송
            return;
        }

        // 일반입점 신품 자전거 코드 설정
        this.product.setProductType("P05"); // 신품
    }

    public void setProductCode(String productCode) {
        // 필수 데이터 입력 부분
        this.product.setProductCode(productCode);
        this.sellInfo.setProductCode(productCode);
        this.infoNotice.setProductCode(productCode);
        this.guide.setProductCode(productCode);
        if (!ObjectUtils.isEmpty(this.optionList))
            Optional.ofNullable(this.optionList).orElseGet(Collections::emptyList)
                    .forEach(option -> option.setProductCode(productCode));
        if (!ObjectUtils.isEmpty(this.delivery))
            this.delivery.setProductCode(productCode);
        if (!ObjectUtils.isEmpty(this.basicSpec))
            this.basicSpec.setProductCode(productCode);
    }

    /**
     * 상품 등록 시 상품 변경이력 정보에 상품 등록으로 처리한다.
     * 상태값은 이력 로그 코드는 등록이고 상품 상태 코드는 입점몰 입력 시 검수요청, 라이트브라더스
     * 등록 시 판매 중 으로 로그 등록 처리.
     *
     * @return 상품 변경 이력 DTO
     */
    public ChangeInfoDto.ReqBody toChangeInfo() {
        return ChangeInfoDto.ReqBody.builder()
                .productCode(this.getProduct().getProductCode())
                .productStatusCode(this.sellInfo.getProductStatusCode())
                .productLogCode(ProductConst.Log.REGISTER.getCode())
                .productLog("상품 등록")
                .userId(this.getProduct().getUserId())
                .build();
    }
}
