package kr.wrightbrothers.apps.template.dto;

import kr.wrightbrothers.apps.common.AbstractPageDto;
import kr.wrightbrothers.apps.common.type.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class TemplateListDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class Param extends AbstractPageDto {
        /** 파트너 코드 */
        private String partnerCode;

        /** 현재 페이지 */
        private int page;

        /** 페이지 행 수 */
        private int count;

        /** 템플릿 구분 */
        private String[] templateType;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        /** 템플릿 번호 */
        private Long templateNo;

        /** 템플릿 구분 */
        private String templateType;

        /** 템플릿 명 */
        private String templateName;

        /** 작성일시 */
        private String createDate;

        // 템플릿 구분 ENUM 처리
        public void setTemplateType(String templateType) {
            this.templateType = TemplateType.of(templateType).getName();
        }
    }

}
