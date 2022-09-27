package kr.wrightbrothers.apps.template.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateInsertDto extends TemplateDto {
    @JsonIgnore
    private Long templateNo;        // 템플릿 번호
    @JsonIgnore
    private String partnerCode;     // 파트너 코드
    @JsonIgnore
    private String userId;          // 사용자 아이디

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
