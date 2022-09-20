package kr.wrightbrothers.apps.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String addressName;         // 주소록 이름
    private String addressZipCode;      // 주소 우편번호
    private String address;             // 주소
    private String addressDetail;       // 상세주소
    private String addressPhone;        // 주소지 연락처
    private String repUnstoringFlag;    // 대표 출고지 주소 지정 여부
    private String repReturnFlag;       // 대표 반품/교환지 주소 지정 여부
}
