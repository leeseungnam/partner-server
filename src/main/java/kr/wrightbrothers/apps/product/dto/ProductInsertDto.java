package kr.wrightbrothers.apps.product.dto;

import kr.wrightbrothers.apps.common.type.CategoryCode;
import kr.wrightbrothers.apps.common.type.ProductLogCode;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.common.type.ProductType;
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
import java.util.List;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInsertDto {
    @Valid
    @NotNull(message = "상품 정보")
    private ProductDto.ReqBody product;         // 상품 기본 정보
    private BasicSpecDto.ReqBody basicSpec;     // 기본 스펙 정보
    @Valid
    @NotNull(message = "판매 정보")
    private SellInfoDto.ReqBody sellInfo;       // 판매 정보
    private List<OptionDto.ReqBody> optionList; // 옵션 정보
    private DeliveryDto.ReqBody delivery;       // 배송 정보
    @Valid
    @NotNull(message = "상품 정보 고시")
    private InfoNoticeDto.ReqBody infoNotice;   // 상품 정보 고시
    @Valid
    @NotNull(message = "안내 사항 정보")
    private GuideDto.ReqBody guide;             // 안내사항 정보
    private List<FileUpdateDto> fileList;       // 상품 등록 이미지

    public void validProduct() {
        // 자전거 상품 추가 유효성 검사
        validBike();
        // 재생자전거 유효성 검사 제외
        if (ProductType.RECYCLING.getType().equals(this.product.getProductType())) return;
        // 배송정보 유효성 검사
        if (ObjectUtils.isEmpty(delivery))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"배송 정보"});
        if (ObjectUtils.isEmpty(delivery.getDeliveryType()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"배송방법"});
        if (ObjectUtils.isEmpty(delivery.getDeliveryBundleFlag()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"묶음배송"});
        if (ObjectUtils.isEmpty(delivery.getChargeType()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"배송비 설정"});
        if (ObjectUtils.isEmpty(delivery.getChargeBase()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"기본 배송비"});
        if (ObjectUtils.isEmpty(delivery.getPaymentType()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"결제방식"});
        if (ObjectUtils.isEmpty(delivery.getSurchargeFlag()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"제주/도서산간 추가 배송비 여부"});
        if (ObjectUtils.isEmpty(delivery.getUnstoringAddress()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"출고지"});
        if (ObjectUtils.isEmpty(delivery.getReturnAddress()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"반품지"});
        if (ObjectUtils.isEmpty(delivery.getExchangeCharge()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"교환배송비"});
        if (ObjectUtils.isEmpty(delivery.getReturnCharge()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"반품배송비(편도)"});
        if (ObjectUtils.isEmpty(delivery.getReturnDeliveryCompanyCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"반품/교환 택배사"});
    }

    // 자전거 상품 추가 유효성 검사
    public void validBike() {
        // 자전거 상품이 아닐경우 제외
        if (!CategoryCode.BIKE.getCode().equals(this.product.getCategoryOneCode()))
            return;

        if (ObjectUtils.isEmpty(basicSpec))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"기본 스펙"});
        if (ObjectUtils.isEmpty(basicSpec.getSalesCategoryCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"완차 구분"});
        if (ObjectUtils.isEmpty(basicSpec.getDrivetrainTypeCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"구동계"});
        if (ObjectUtils.isEmpty(basicSpec.getFrameMaterialCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"프레임 소재"});
        if (ObjectUtils.isEmpty(basicSpec.getFrameSizeCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"프레임 사이즈"});
        if (ObjectUtils.isEmpty(basicSpec.getBrakeTypeCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"브레이크 타입"});
        if (ObjectUtils.isEmpty(basicSpec.getPurposeThemeCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"용도 테마"});
        if (ObjectUtils.isEmpty(basicSpec.getWheelSizeCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"휠 사이즈"});
        if (ObjectUtils.isEmpty(basicSpec.getSuspensionTypeCode()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"서스펜션"});
        if (ObjectUtils.isEmpty(basicSpec.getMinHeightPerson()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"호환키"});
        if (ObjectUtils.isEmpty(basicSpec.getMaxHeightPerson()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"호환키"});
        if (ObjectUtils.isEmpty(basicSpec.getBikeWeight()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"무게"});
        if (ObjectUtils.isEmpty(basicSpec.getAgeList()))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"탑승 연령대"});
    }

    public void setUserId(String userId) {
        // 필수 데이터 입력 부분
        product.setUserId(userId);
        sellInfo.setUserId(userId);
        delivery.setUserId(userId);
        infoNotice.setUserId(userId);
        guide.setUserId(userId);
        optionList.forEach(option -> option.setUserId(userId));
        fileList.forEach(file -> file.setUserId(userId));
        if (!ObjectUtils.isEmpty(basicSpec))
            basicSpec.setUserId(userId);
    }

    public void setProductCode(String productCode) {
        // 필수 데이터 입력 부분
        product.setProductCode(productCode);
        sellInfo.setProductCode(productCode);
        delivery.setProductCode(productCode);
        infoNotice.setProductCode(productCode);
        guide.setProductCode(productCode);
        optionList.forEach(option -> option.setProductCode(productCode));

        if (!ObjectUtils.isEmpty(productCode))
            basicSpec.setProductCode(productCode);
    }

    /**
     * 상품 등록 시 상품 변경이력 정보에 상품 등록으로 처리한다.
     * 상태값은 이력 로그 코드는 등록, 상품 상태 코드는 검수대기 처리한다.
     *
     * @return 상품 변경 이력 DTO
     */
    public ChangeInfoDto.ReqBody toChangeInfo() {
        return ChangeInfoDto.ReqBody.builder()
                .productCode(this.getProduct().getProductCode())
                .productStatusCode(ProductStatusCode.PRODUCT_INSPECTION.getCode())
                .productLogCode(ProductLogCode.REGISTER.getCode())
                .productLog("상품 등록")
                .userId(this.getProduct().getUserId())
                .build();
    }
}
