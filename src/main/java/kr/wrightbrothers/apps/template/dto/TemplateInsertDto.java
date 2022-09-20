package kr.wrightbrothers.apps.template.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import kr.wrightbrothers.apps.product.dto.DeliveryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateInsertDto {
    @ApiModelProperty(value = "템플릿 배송 정보")
    private TemplateDeliveryDto delivery;
    @ApiModelProperty(value = "템플릿 구분", required = true)
    private String templateType;
    @ApiModelProperty(value = "템플릿 이름", required = true)
    private String templateName;
    @ApiModelProperty(value = "템플릿 안내 정보")
    private String templateGuide;
    @JsonIgnore
    private Long templateNo;
    @JsonIgnore
    private String partnerCode;
    @JsonIgnore
    private String userId;

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
