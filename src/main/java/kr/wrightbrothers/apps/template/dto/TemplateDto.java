package kr.wrightbrothers.apps.template.dto;

import kr.wrightbrothers.apps.common.type.TemplateType;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto {
    @NotBlank(message = "템플릿 구분")
    private String templateType;
    @NotBlank(message = "템플릿 이름")
    @Size(min = 2, max = 50, message = "템플릿 이름")
    private String templateName;
    private String templateGuide;
    private TemplateDeliveryDto delivery;

    public void validTemplate() {
        // 가이드 템플릿 경우 체크
        if (!TemplateType.DELIVERY.getType().equals(this.templateType)) {
            if (ObjectUtils.isEmpty(this.templateGuide))
                throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"안내 내용"});
            return;
        }

        // 배송정보 유효성 검사
        delivery.validTemplateDelivery();
    }

}
