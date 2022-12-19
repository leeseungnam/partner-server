package kr.wrightbrothers.apps.address.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class AddressAuthDto {
    /** 파트너 코드 */
    private String partnerCode;

    /** 주소록 번호 */
    private Long addressNo;
}
