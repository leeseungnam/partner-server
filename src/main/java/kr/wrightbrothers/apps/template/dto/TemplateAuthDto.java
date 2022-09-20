package kr.wrightbrothers.apps.template.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemplateAuthDto {
    private String partnerCode;     // 파트너 코드
    private Long templateNo;        // 템플릿 번호
}
