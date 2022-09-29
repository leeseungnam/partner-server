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
        private String partnerCode;         // 파크너 코드
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long addressNo;             // 주소록 번호
        private String addressName;         // 주소록 명
        private String addressZipCode;      // 우편번호
        private String address;             // 주소
        private String addressDetail;       // 상세주소
        private String addressPhone;        // 주소지 연락처
        private String reqUnstoringFlag;    // 대표 출고지 주소 지정 여부
        private String reqReturnFlag;       // 대표 반품/교환지 주소 지정 여부
    }

}
