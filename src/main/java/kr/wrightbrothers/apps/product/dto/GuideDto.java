package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class GuideDto {

    @Getter @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Guide {
        /** 상품 상세설명 */
        @NotBlank(message = "상품 상세설명")
        private String productDescription;

        /** 안내 사항 */
        @NotBlank(message = "안내 사항")
        @Size(min = 30, max = 2000, message = "안내 사항")
        private String productGuide;

        /** 배송 안내 */
        @NotBlank(message = "배송 안내")
        @Size(min = 30, max = 2000, message = "배송 안내")
        private String deliveryGuide;

        /** 교환/반품 안내 */
        @Size(max = 2000, message = "교환/반품 안내")
        private String exchangeReturnGuide;

        /** A/S 안내 */
        @NotBlank(message = "A/S 안내")
        private String asGuide;

        /** 자주 묻는 질문 */
        @Size(max = 2000, message = "교환/반품 안내")
        private String qnaGuide;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Guide {
        /** 상품 코드 */
        @JsonIgnore
        private String productCode;

        /** 사용자 아이디 */
        @JsonIgnore
        private String userId;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends Guide {}

}
