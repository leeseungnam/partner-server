package kr.wrightbrothers.apps.product.dto;

import kr.wrightbrothers.apps.common.type.ProductLogCode;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto extends ProductInsertDto {
    /** 상품 코드 */
    @NotNull(message = "상품 코드")
    private String productCode;

    /** 변경 로그 */
    private String[] changeLogList;

    /**
     * 상품 수정 시 상품 변경이력 정보에 변경 정보를 등록한다.
     * 상품 변경 내용에 대한 정보 / 상태값을 등록하고 로드 코드는 수정으로 처리한다.
     *
     * @return 상품 변경 이력 DTO
     */
    public ChangeInfoDto.ReqBody toChangeInfo() {
        String productLogCode = ProductLogCode.MODIFY.getCode();

        if (ProductStatusCode.REJECT_INSPECTION.getCode().equals(this.getSellInfo().getProductStatusCode()))
            productLogCode = ProductLogCode.REJECT.getCode();

        if (!ObjectUtils.isEmpty(changeLogList) && StringUtils.join(changeLogList, ", ").contains("검수 완료"))
            productLogCode = ProductLogCode.INSPECTION.getCode();

        return ChangeInfoDto.ReqBody.builder()
                .productCode(this.productCode)
                .productStatusCode(this.getSellInfo().getProductStatusCode())
                .productLogCode(productLogCode)
                .productLog(StringUtils.join(changeLogList, ", "))
                .userId(this.getProduct().getUserId())
                .build();
    }

    public void setSqsLog(String[] sqsLog) {
        this.changeLogList = sqsLog;
    }

    public void setSqsProductCode(String productCode) {
        this.productCode = productCode;
        this.setProductCode(productCode);
    }

    public ProductFindDto.Param toProductFindParam() {
        return new ProductFindDto.Param(this.getProduct().getPartnerCode(), this.productCode);
    }
}
