package kr.wrightbrothers.apps.template.dto;

import kr.wrightbrothers.apps.product.dto.DeliveryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class TemplateFindDto {

    @Getter
    @Builder
    public static class Param {
        private String partnerCode;         // 스토어 코드
        private Long templateNo;            // 템플릿 번호
    }

    @Data
    @Getter
    public static class Response {
        private String templateType;            // 템플릿 구분
        private String templateName;            // 템플릿 이름
        private String templateGuide;           // 템플릿 안내 설명
        private DeliveryDto.ResBody delivery;   // 배송
    }

}
