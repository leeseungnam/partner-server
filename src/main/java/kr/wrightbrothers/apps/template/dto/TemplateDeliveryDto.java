package kr.wrightbrothers.apps.template.dto;

import kr.wrightbrothers.apps.product.dto.DeliveryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
public class TemplateDeliveryDto extends DeliveryDto.Delivery {}
