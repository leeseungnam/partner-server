package kr.wrightbrothers.apps.template.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemplateDeleteDto {
    private Long[] templateNoList;    // 템플릿 번호 목록
    private String partnerCode;       // 파트너 코그
    private String userId;            // 사용자 아이디
}
