package kr.wrightbrothers.apps.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReceiveDto {
    @JsonProperty("ProductMain")
    private SqsProductDto product;

    @JsonProperty("ProductBasicSpecification")
    private SqsBasicSpecDto basicSpec;

    @JsonProperty("ProductSellInformation")
    private SqsSellInfoDto sellInfo;

    @JsonProperty("ProductOptin")
    private List<SqsOptionDto> optionList;

    @JsonProperty("ProductDeliveryDetail")
    private SqsDeliveryDto delivery;

    @JsonProperty("ProductInformationBulletin")
    private SqsInfoNoticeDto infoNotice;

    @JsonProperty("ProductGuideanceComment")
    private SqsGuideDto guide;

    @JsonProperty("ProductEmbarkAge")
    private List<Age> ageList;

    @JsonProperty("ProductDelivery")
    private List<DeliveryCode> deliveryList;

    @Getter
    public static class Age {
        @JsonProperty("EmbarkAge")
        private String age;
    }

    @Getter
    public static class DeliveryCode {
        @JsonProperty("ProductDeliveryCode")
        private String code;
    }
}
