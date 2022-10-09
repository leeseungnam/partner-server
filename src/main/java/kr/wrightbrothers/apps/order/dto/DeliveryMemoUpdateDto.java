package kr.wrightbrothers.apps.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
public class DeliveryMemoUpdateDto extends OrderUpdateDto {
    @ApiModelProperty(value = "배송메모", required = false)
    @Size(max = 2000, message = "배송메모")
    private String deliveryMemo;            // 배송 메모
}
