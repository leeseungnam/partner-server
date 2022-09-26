package kr.wrightbrothers.apps.address.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateDto extends AddressDto {
    @ApiModelProperty(value = "주소록 번호")
    @NotNull(message = "주소록 번호")
    private Long addressNo;
    private String partnerCode;
    @JsonIgnore
    private String userId;
}
