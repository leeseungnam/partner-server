package kr.wrightbrothers.apps.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqsSellInfoDto {
    @JsonProperty("ProductAmount")
    private Long productAmount;

    @JsonProperty("DiscountFlag")
    private String discountFlag;

    @JsonProperty("DiscountType")
    private String discountType;

    @JsonProperty("DiscountAmount")
    private String discountAmount;

    @JsonProperty("DisplayFlag")
    private String displayFlag;

    private String productOptionFlag;

    @JsonProperty("PurchaseAmount")
    private Long supplyAmount;

    @JsonProperty("FinalSellAmount")
    private Long finalSellAmount;

    @JsonProperty("ProductStatusCode")
    private String productStatusCode;

    @JsonProperty("InventoryQuantity")
    private int productStockQty;

    @JsonProperty("ProductSellStartDate")
    private String productSellStartDate;
}
