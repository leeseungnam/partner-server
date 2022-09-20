package kr.wrightbrothers.apps.address.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressDeleteDto {
    private String partnerCode;
    private Long addressNo;
}
