package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

public class InfoNoticeDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InfoNotice {
        @NotBlank(message = "상품구분")
        private String categoryCode;
        @NotBlank(message = "품명/모델명")
        private String modelName;
        private String productSize;
        private String productWeight;
        private String productMaterial;
        private String productComponent;
        @NotBlank(message = "출시 연도")
        private String modelYear;
        @NotBlank(message = "출시 월")
        private String modelMonth;
        @NotBlank(message = "제조자(사)")
        private String productMfr;
        private String detailSpec;
        private String qaStandard;
        @NotBlank(message = "AS 연락처")
        private String asPhone;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends InfoNotice {
        @JsonIgnore
        private String productCode;         // 상품 코드
        @JsonIgnore
        private String userId;              // 작성자 아이디
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends InfoNotice {}

}
