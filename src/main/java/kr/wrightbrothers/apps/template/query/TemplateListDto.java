package kr.wrightbrothers.apps.template.query;

import kr.wrightbrothers.apps.common.AbstractPageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class TemplateListDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class Param extends AbstractPageDto {
        private String[] templateType;
    }

}
