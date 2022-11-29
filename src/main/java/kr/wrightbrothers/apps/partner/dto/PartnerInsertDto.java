package kr.wrightbrothers.apps.partner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerInsertDto {

    @ApiModelProperty(value = "파트너 정보", required = true)
    @Valid
    @NotNull(message = "파트너 정보")
    private PartnerDto.ReqBody partner;

    @ApiModelProperty(value = "계약 정보", required = true)
    @Valid
    @NotNull(message = "계약 정보")
    private PartnerContractDto.ReqBody partnerContract;

    @JsonIgnore
    private PartnerRejectDto.Param partnerReject;

    @JsonIgnore
    public void setAopUserId(String userId) {
        // 작성자 아이디 SET
        partner.changeUserId(userId);
        partnerContract.changeUserId(userId);

        if(!ObjectUtils.isEmpty(partnerReject)) partnerReject.changeUserId(userId);
    }
}
