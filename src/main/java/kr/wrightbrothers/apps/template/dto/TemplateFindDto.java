package kr.wrightbrothers.apps.template.dto;

import lombok.*;

public class TemplateFindDto {

    @Getter @Builder
    public static class Param {
        /** 파트너 코드 */
        private String partnerCode;

        /** 템플릿 번호 */
        private Long templateNo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        /** 템플릿 구분 */
        private String templateType;

        /** 템플릿 이름 */
        private String templateName;

        /** 템플릿 안내 설명 */
        private String templateGuide;

        /** 배송 */
        private TemplateDeliveryDto delivery;
    }

}
