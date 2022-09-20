package kr.wrightbrothers.apps.address.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressAuthDto {
    private String partnerCode;     // 파트너 코드
    private Long addressNo;         // 주소록 번호
}
