package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelReturnDto {
    @NotBlank(message = "주문 번호")
    private String orderNo;                 // 주문번호

    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray; // 주문 상품 SEQ

    private String partnerCode;             // 파트너 코드
    @JsonIgnore
    private String userId;                  // 사용자 아이디

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setAopUserId(String userId) {
        this.userId = userId;
    }


    public RequestReturnUpdateDto toRequestReturnUpdateDto(String processCode) {
        return RequestReturnUpdateDto.builder()
                .orderNo(this.orderNo)
                .orderProductSeqArray(this.orderProductSeqArray)
                .partnerCode(this.partnerCode)
                .returnProcessCode(processCode)
                .userId(this.userId)
                .build();
    }
}
