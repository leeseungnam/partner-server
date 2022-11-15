package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@ApiModel(value = "주문내역 수정")
public class OrderMemoUpdateDto {
    @ApiModelProperty(value = "주문 번호", required = true)
    @NotBlank(message = "주문 번호")
    private String orderNo;                 // 주문 번호

    @ApiModelProperty(value = "주문메모", required = false)
    @Size(max = 2000, message = "주문메모")
    private String orderMemo;               // 주문 메모

    private String partnerCode;             // 파트너 코드
    @JsonIgnore
    private String userId;                  // 사용자 아이디

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
    public void setAopUserId(String userId) {
        this.userId = userId;
    }
}
