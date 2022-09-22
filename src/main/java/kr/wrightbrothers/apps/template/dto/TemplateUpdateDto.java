package kr.wrightbrothers.apps.template.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.product.dto.DeliveryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateUpdateDto {
    @NotNull(message = "템플릿 번호")
    private Long templateNo;
    @NotBlank(message = "템플릿 구분")
    private String templateType;
    @Size(min = 2, max = 50, message = "템플릿 이름")
    @NotBlank(message = "템플릿 이름")
    private String templateName;
    private String templateGuide;
    private TemplateDeliveryDto delivery;
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
