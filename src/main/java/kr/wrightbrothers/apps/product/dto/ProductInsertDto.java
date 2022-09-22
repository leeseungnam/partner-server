package kr.wrightbrothers.apps.product.dto;

import kr.wrightbrothers.apps.common.type.ProductLogCode;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.file.dto.FileUpdateDto;
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
    @Valid
    @NotNull(message = "배송 정보")
    private DeliveryDto.ReqBody delivery;       // 배송 정보
    @Valid
    @NotNull(message = "상품 정보 고시")
    private InfoNoticeDto.ReqBody infoNotice;   // 상품 정보 고시
    @Valid
    @NotNull(message = "안내 사항 정보")
    private GuideDto.ReqBody guide;             // 안내사항 정보
    private List<FileUpdateDto> fileList;       // 상품 등록 이미지

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
