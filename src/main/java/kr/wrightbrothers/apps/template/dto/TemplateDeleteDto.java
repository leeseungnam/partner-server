package kr.wrightbrothers.apps.template.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class TemplateDeleteDto {
    /** 템플릿 번호 */
    private Long[] templateNoList;

    /** 파트너 코드 */
    private String partnerCode;

    /** 사용자 아이디 */
    private String userId;
}
