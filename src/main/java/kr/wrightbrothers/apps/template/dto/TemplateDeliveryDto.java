package kr.wrightbrothers.apps.template.dto;

import kr.wrightbrothers.apps.product.dto.DeliveryDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDeliveryDto extends DeliveryDto.Delivery {
    private String unstoringZipCode;
    private String unstoringAddressDetail;
    private String returnZipCode;
    private String returnAddressDetail;
}
