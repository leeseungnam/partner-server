package kr.wrightbrothers.apps.template.dto;

import kr.wrightbrothers.apps.common.AbstractPageDto;
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
        private String partnerCode;
        private int page;
        private int count;
        private String[] templateType;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long templateNo;
        private String templateType;
        private String templateName;
        private String create_date;
    }

}
