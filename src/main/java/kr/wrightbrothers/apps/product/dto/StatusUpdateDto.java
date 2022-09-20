package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateDto {
    private String[] productCodeList;   // 변경 상품 코드
    private String partnerCode;         // 스토어 코드
    private String changeType;          // 변경 구분
    private String changeValue;         // 변경 값

    @JsonIgnore
    private String userId;              // 변경자

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
}
