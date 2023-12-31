package kr.wrightbrothers.apps.address.dto;

import kr.wrightbrothers.apps.common.AbstractPageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class AddressListDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class Param extends AbstractPageDto {
        /** 파트너 코드 */
        private String partnerCode;

        /** 키워드 구분 */
        private String searchType;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        /** 주소록 번호 */
        private Long addressNo;

        /** 주소록 명 */
        private String addressName;

        /** 우편번호 */
        private String addressZipCode;

        /** 주소 */
        private String address;

        /** 상세주소 */
        private String addressDetail;

        /** 연락처 */
        private String addressPhone;

        /** 대표 출고지 여부 */
        private String repUnstoringFlag;

        /** 대표 반품/교환지 여부 */
        private String repReturnFlag;
    }

}
