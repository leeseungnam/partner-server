package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.json.JSONObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class InfoNoticeDto {

    @Getter
    @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class InfoNotice {
        @ApiModelProperty(value = "상품구분", required = true)
        @NotBlank(message = "상품구분")
        private String categoryCode;

        @ApiModelProperty(value = "품명/모델명", required = true)
        @NotBlank(message = "품명/모델명")
        private String modelName;

        @ApiModelProperty(value = "크기")
        private String productSize;

        @ApiModelProperty(value = "중량")
        private String productWeight;

        @ApiModelProperty(value = "재질")
        private String productMaterial;

        @ApiModelProperty(value = "제품구성")
        private String productComponent;

        @ApiModelProperty(value = "출시 연도", required = true)
        @NotBlank(message = "출시 연도")
        private String modelYear;

        @ApiModelProperty(value = "출시 월", required = true)
        @NotBlank(message = "출시 월")
        private String modelMonth;

        @ApiModelProperty(value = "제조자(사)", required = true)
        @Size(min = 2, max = 50, message = "제조자(사)")
        @NotBlank(message = "제조자(사)")
        private String productMfr;

        @ApiModelProperty(value = "세부사양")
        private String detailSpec;

        @ApiModelProperty(value = "품질보증기준")
        private String qaStandard;

        @ApiModelProperty(value = "AS 연락처", required = true)
        @Size(min = 2, max = 20, message = "AS 연락처")
        @NotBlank(message = "AS 연락처")
        private String asPhone;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @ApiModel(value = "상품 정보 고시")
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
