package kr.wrightbrothers.apps.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqsOptionDto {
    @JsonProperty("OptionSequence")
    private Integer optionSeq;          // 옵션 SEQ

    @JsonProperty("OptionName")
    private String optionName;          // 옵션 명

    @JsonProperty("OptionValue")
    private String optionValue;         // 옵션 값

    @JsonProperty("OptionSurcharge")
    private Long optionSurcharge;       // 옵션 추가금

    @JsonProperty("InventoryQuantity")
    private Integer optionStockQty;     // 재고 수량
}
