package kr.wrightbrothers.apps.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParamDto {
    private String partnerCode;
    private String orderNo;
}
