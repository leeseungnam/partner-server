package kr.wrightbrothers.apps.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Size;

@Getter
@Jacksonized
@SuperBuilder
public class ReturnMemoUpdateDto extends OrderUpdateDto {
    @ApiModelProperty(value = "반품메모", required = false)
    @Size(max = 2000, message = "반품메모")
    private String returnMemo;          // 반품 메모
}
