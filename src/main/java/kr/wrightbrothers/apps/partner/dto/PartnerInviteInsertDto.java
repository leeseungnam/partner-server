package kr.wrightbrothers.apps.partner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerInviteInsertDto {

    @ApiModelProperty(value = "파트너 정보", required = true)
    @Valid
    @NotNull(message = "파트너 정보")
    private PartnerInviteDto.ReqBody partnerOperator;

    @JsonIgnore
    public void setAopUserId(String userId) {
        // 작성자 아이디 SET
        partnerOperator.changeUserId(userId);
    }
}
