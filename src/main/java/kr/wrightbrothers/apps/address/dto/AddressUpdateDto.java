package kr.wrightbrothers.apps.address.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter @Setter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateDto extends AddressDto {
    /** 주소록 번호 */
    @NotNull(message = "주소록 번호")
    private Long addressNo;

    /** 파트너 코드 */
    private String partnerCode;

    /** 사용자 아이디 */
    @JsonIgnore
    private String userId;

    public void setAopUserId(String userId) {
        this.userId = userId;
    }

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

}
