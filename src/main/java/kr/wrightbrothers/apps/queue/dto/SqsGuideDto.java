package kr.wrightbrothers.apps.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqsGuideDto {
    @JsonProperty("ProductGuideanceCommentTwo")
    private String productGuide;                    // 상품 설명

    @JsonProperty("ProductGuideanceCommentFour")
    private String deliveryGuide;                   // 배송 설명

    @JsonProperty("ExchangeReturnGuide")
    private String exchangeReturnGuide;             // 교환/반품 설명

    @JsonProperty("AsGuide")
    private String asGuide;                         // AS 설명

    @JsonProperty("ProductGuideanceCommentThree")
    private String qnaGuide;                        // 자주묻는 질문

    @JsonProperty("ProductGuideanceCommentOne")
    private String productDescription;              // 상품 상세 설명
}
