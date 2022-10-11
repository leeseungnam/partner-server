package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestReturnUpdateDto {
    @NotBlank(message = "주문 번호")
    private String orderNo;                 // 주문 번호

    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeq;      // 주문 상품 SEQ

    @NotBlank(message = "반품 요청 처리 구분")
    private String returnProcessCode;       // 반품 요청 처리 코드

    private String nonReturnReasonCode;     // 반품 불가 사유 코드
    private String nonReturnReasonName;     // 반품 불가 사유 이름

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

