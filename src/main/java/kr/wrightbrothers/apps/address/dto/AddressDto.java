package kr.wrightbrothers.apps.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    /** 주소록 이름 */
    @Size(min = 2, max = 30, message = "주소록 이름")
    @NotBlank(message = "주소록 이름")
    private String addressName;

    /** 우편번호 */
    @NotBlank(message = "우편번호")
    private String addressZipCode;

    /** 주소 */
    @NotBlank(message = "주소")
    private String address;

    /** 상세주소 */
    @NotBlank(message = "상세주소")
    private String addressDetail;

//    @ApiModelProperty(value = "연락처", required = true)
//    @Pattern(regexp = "^\\d+$", message = "연락처는 숫자만 입력 가능 합니다.")
//    @Size(min = 8, max = 20, message = "연락처")
//    @NotBlank(message = "연락처")
    /** 연락처 */
    private String addressPhone;

    /** 대표 출고지 여부 */
    @NotBlank(message = "대표 출고지 주소로 지정")
    private String repUnstoringFlag;

    /** 대표 반품/교환지 여부 */
    @NotBlank(message = "대표 반품/교환지 주소로 지정")
    private String repReturnFlag;
}
