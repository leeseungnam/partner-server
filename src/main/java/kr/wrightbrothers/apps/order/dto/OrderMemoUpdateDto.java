package kr.wrightbrothers.apps.order.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
@ApiModel(value = "주문내역 수정")
public class OrderMemoUpdateDto extends OrderUpdateDto {
    @ApiModelProperty(value = "주문메모", required = false)
    @Size(max = 2000, message = "주문메모")
    private String orderMemo;               // 주문 메모
}
