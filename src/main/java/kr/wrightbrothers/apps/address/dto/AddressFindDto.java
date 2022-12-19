package kr.wrightbrothers.apps.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class AddressFindDto {

    @Getter @Builder
    public static class Param {
        /** 파트너 코드 */
        private String partnerCode;

        /** 주소록 번호 */
        private Long addressNo;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response extends AddressDto {
        /** 주소록 번호 */
        private Long addressNo;
    }

}
