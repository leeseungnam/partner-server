package kr.wrightbrothers.apps.address.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressInsertDto extends AddressDto {
    @JsonIgnore
    private Long addressNo;         // 주소록 번호
    @JsonIgnore
    private String partnerCode;     // 파트너 코드
    @JsonIgnore
    private String userId;          // 사용자 아이디
}
