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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateUpdateDto extends TemplateDto {
    @ApiModelProperty(value = "템플릿 번호", required = true)
    @NotNull(message = "템플릿 번호")
    private Long templateNo;
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
