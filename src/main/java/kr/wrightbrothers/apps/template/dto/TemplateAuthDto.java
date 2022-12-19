package kr.wrightbrothers.apps.template.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class TemplateAuthDto {
    /** 파트너 코드 */
    private String partnerCode;

    /** 템플릿 번호 */
    private Long templateNo;
}
