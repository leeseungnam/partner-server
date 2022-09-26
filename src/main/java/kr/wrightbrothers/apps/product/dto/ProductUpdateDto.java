package kr.wrightbrothers.apps.product.dto;

import io.swagger.annotations.ApiModelProperty;
import kr.wrightbrothers.apps.common.type.ProductLogCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto extends ProductInsertDto {
    @ApiModelProperty(value = "상품 코드", required = true)
    @NotNull(message = "상품 코드")
    private String productCode;     // 상품 코드

    @ApiModelProperty(value = "변경 로그")
    private String[] changeLogList; // 변경 로그

    /**
     * 상품 수정 시 상품 변경이력 정보에 변경 정보를 등록한다.
     * 상품 변경 내용에 대한 정보 / 상태값을 등록하고 로드 코드는 수정으로 처리한다.
     *
     * @return 상품 변경 이력 DTO
     */
    public ChangeInfoDto.ReqBody toChangeInfo() {
        return ChangeInfoDto.ReqBody.builder()
                .productCode(this.productCode)
                .productStatusCode(this.getSellInfo().getProductStatusCode())
                .productLogCode(ProductLogCode.MODIFY.getCode())
                .productLog(StringUtils.join(changeLogList, ", "))
                .userId(this.getProduct().getUserId())
                .build();
    }
}
