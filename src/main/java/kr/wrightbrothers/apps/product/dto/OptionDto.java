package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class OptionDto {

    @Getter @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        /** 옵션 번호 */
        private Integer optionSeq;

        /** 옵션 명 */
        @Size(min = 0, max = 20, message = "옵션 명")
        private String optionName;

        /** 옵션 항목 */
        @Size(min = 0, max = 20, message = "옵션 항목")
        private String optionValue;

        /** 변동 금액 */
        @Min(value = -10000000, message = "변동 금액")
        @Max(value = 10000000, message = "변동 금액")
        private Long optionSurcharge;

        /** 옵션 재고 수량 */
        @Min(value = 0, message = "옵션 재고 수량")
        @Max(value = 9999, message = "옵션 재고 수량")
        private Integer optionStockQty;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Option {
        /** 상품코드 */
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
    public static class ResBody extends Option {}

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class Queue extends Option {
        private String matadata;
    }
}
