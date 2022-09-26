package kr.wrightbrothers.apps.template.dto;

import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.product.dto.DeliveryDto;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

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

    public void validTemplateDelivery() {
        // 기본 배송 유효성 체크
        validDelivery();

        if (ObjectUtils.isEmpty(this.unstoringZipCode))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"출고지 우편번호"});
        if (ObjectUtils.isEmpty(this.returnZipCode))
            throw new WBBusinessException(ErrorCode.INVALID_PARAM.getErrCode(), new String[]{"반품지 우편번호"});

        if (!ObjectUtils.isEmpty(this.unstoringAddressDetail) && this.unstoringAddressDetail.length() > 100)
            throw new WBBusinessException(ErrorCode.INVALID_TEXT_SIZE.getErrCode(), new String[]{"출고지 상세주소", "0", "100"});
        if (!ObjectUtils.isEmpty(this.returnAddressDetail) && this.returnAddressDetail.length() > 100)
            throw new WBBusinessException(ErrorCode.INVALID_TEXT_SIZE.getErrCode(), new String[]{"반품지 상세주소", "0", "100"});
    }
}
