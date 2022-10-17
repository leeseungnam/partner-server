package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class GuideDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Guide {
        @ApiModelProperty(value = "안내 사항", required = true)
        @NotBlank(message = "안내 사항")
        @Size(min = 30, max = 2000, message = "안내 사항")
        private String productGuide;

        @ApiModelProperty(value = "배송 안내",required = true)
        @NotBlank(message = "배송 안내")
        @Size(min = 30, max = 2000, message = "배송 안내")
        private String deliveryGuide;

        @ApiModelProperty(value = "교환/반품 안내")
        @Size(max = 2000, message = "교환/반품 안내")
        private String exchangeReturnGuide;

        @ApiModelProperty(value = "A/S 안내", required = true)
        @NotBlank(message = "A/S 안내")
        private String asGuide;

        @ApiModelProperty(value = "자주 묻는 질문")
        @Size(max = 2000, message = "교환/반품 안내")
        private String qnaGuide;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @ApiModel(value = "상품 안내 사항")
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Guide {
        @JsonIgnore
        private String productCode;
        @JsonIgnore
        private String userId;

        public static GuideDto.ReqBody jsonToGuideDto(JSONObject object) {
            if (ObjectUtils.isEmpty(object.getJSONObject("ProductGuideanceComment"))) return null;

            return ReqBody.builder()
                    .productCode(object.getJSONObject("ProductMain").getString("ProductCode"))
                    .userId(object.getJSONObject("ProductMain").getString("CreateUserId"))
                    .productGuide(object.getJSONObject("ProductMain").getString("ProductGuideanceCommentTwo"))
                    .deliveryGuide(object.getJSONObject("ProductMain").getString("ProductGuideanceCommentFour"))
                    .exchangeReturnGuide(object.getJSONObject("ProductMain").getString("ExchangeReturnGuide"))
                    .asGuide(object.getJSONObject("ProductMain").getString("AsGuide"))
                    .qnaGuide(object.getJSONObject("ProductMain").getString("ProductGuideanceCommentThree"))
                    .build();
        }
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends Guide {}

}
