package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SellInfoDto {

    @Getter @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class SellInfo {
        /** 상품금액 */
        @Max(value = 100000000, message = "상품금액")
        @Min(value = 100, message = "상품금액")
        @NotNull(message = "상품금액")
        private Long productAmount;

        /** 할인여부 */
        @NotBlank(message = "할인여부")
        private String discountFlag;

        /** 할인구분 */
        private String discountType;

        /** 할인금액 */
        private String discountAmount;

        /** 전시상태 */
        @NotBlank(message = "전시상태")
        private String displayFlag;

        /** 옵션여부 */
        @NotBlank(message = "옵션여부")
        private String productOptionFlag;

        /** 공급금액 */
        @Max(value = 100000000, message = "공급금액")
        private Long supplyAmount;

        /** 판매가 */
        @NotNull(message = "판매가")
        private Long finalSellAmount;

        /** 상품상태 */
        private String productStatusCode;

        /** 재고 */
        @NotNull(message = "재고")
        @Max(value = 9999, message = "재고")
        private Integer productStockQty;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends SellInfo {
        /** 상품 코드 */
        @JsonIgnore
        private String productCode;

        /** 사용자 아이디 */
        @JsonIgnore
        private String userId;

        /** 판매 시작 일시 */
        @JsonIgnore
        private String productSellStartDate;

        /** 판매 종료 일시 */
        @JsonIgnore
        private String productSellEndDate;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends SellInfo {
        /** 판매 시작 일시 */
        private String productSellStartDate;

        /** 판매 종료 일시 */
        private String productSellEndDate;
    }
}
