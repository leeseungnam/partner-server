package kr.wrightbrothers.apps.template.dto;

import io.swagger.annotations.ApiModelProperty;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.product.dto.DeliveryDto;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
public class TemplateDeliveryDto extends DeliveryDto.Delivery {}
