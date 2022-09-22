package kr.wrightbrothers.apps.template.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import kr.wrightbrothers.apps.product.dto.DeliveryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateInsertDto {
    @ApiModelProperty(value = "템플릿 배송 정보")
    private TemplateDeliveryDto delivery;
    @NotBlank(message = "템플릿 구분")
    @ApiModelProperty(value = "템플릿 구분", required = true)
    private String templateType;
    @NotBlank(message = "템플릿 이름")
    @Size(min = 2, max = 50, message = "템플릿 이름")
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
