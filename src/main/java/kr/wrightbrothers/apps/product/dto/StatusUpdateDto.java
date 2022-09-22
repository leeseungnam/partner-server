package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.type.ProductLogCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateDto {
    @NotEmpty(message = "변경 상품 코드")
    private String[] productCodeList;   // 변경 상품 코드
    private String partnerCode;         // 스토어 코드
    @NotBlank(message = "변경 구분")
    private String statusType;          // 변경 구분
    @NotBlank(message = "변경 값")
    private String statusValue;         // 변경 값

    @JsonIgnore
    private String userId;              // 변경자

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public ChangeInfoDto.ReqBody toChangeInfo(String productCode,
                                              String productStatusCode) {
        return ChangeInfoDto.ReqBody.builder()
                .productCode(productCode)
                .productStatusCode(productStatusCode)
                .productLogCode(ProductLogCode.MODIFY.getCode())
                .productLog("판매 정보 수정")
                .userId(this.getUserId())
                .build();
    }
}