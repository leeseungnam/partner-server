package kr.wrightbrothers.apps.address.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @ApiModelProperty(value = "주소록 이름")
    @Size(min = 2, max = 30, message = "주소록 이름")
    @NotBlank(message = "주소록 이름")
    private String addressName;         // 주소록 이름
    @ApiModelProperty(value = "우편번호")
    @NotBlank(message = "우편번호")
    private String addressZipCode;      // 주소 우편번호
    @ApiModelProperty(value = "주소")
    @NotBlank(message = "주소")
    private String address;             // 주소
    @ApiModelProperty(value = "상세주소")
    @NotBlank(message = "상세주소")
    private String addressDetail;       // 상세주소
    @ApiModelProperty(value = "연락처")
    @Pattern(regexp = "^\\d+$", message = "연락처는 숫자만 입력 가능 합니다.")
    @Size(min = 8, max = 20, message = "연락처")
    @NotBlank(message = "연락처")
    private String addressPhone;        // 주소지 연락처
    @ApiModelProperty(value = "대표 출고지 주소로 지정 여부")
    @NotBlank(message = "대표 출고지 주소로 지정")
    private String repUnstoringFlag;    // 대표 출고지 주소 지정 여부
    @ApiModelProperty(value = "대표 반품/교환지 주소로 지정 여부")
    @NotBlank(message = "대표 반품/교환지 주소로 지정")
    private String repReturnFlag;       // 대표 반품/교환지 주소 지정 여부
}
