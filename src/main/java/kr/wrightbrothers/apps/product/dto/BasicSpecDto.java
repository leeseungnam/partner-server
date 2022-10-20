package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class BasicSpecDto {

    @Getter
    @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class BasicSpec {
        @ApiModelProperty(value = "완차구분")
        private String salesCategoryCode;

        @ApiModelProperty(value = "구동계")
        private String drivetrainTypeCode;

        @ApiModelProperty(value = "프레임 소재")
        private String frameMaterialCode;

        @ApiModelProperty(value = "프레임 사이즈")
        private String frameSizeCode;

        @ApiModelProperty(value = "브레이크 타입")
        private String brakeTypeCode;

        @ApiModelProperty(value = "용도 테마")
        private String purposeThemeCode;

        @ApiModelProperty(value = "휠 사이즈")
        private String wheelSizeCode;

        @ApiModelProperty(value = "서스팬션")
        private String suspensionTypeCode;

        @ApiModelProperty(value = "호환키(최소)")
        private String minHeightPerson;

        @ApiModelProperty(value = "호환키(최대)")
        private String maxHeightPerson;

        @ApiModelProperty(value = "무게")
        private String bikeWeight;

        @ApiModelProperty(value = "탑승 연령대")
        private List<String> ageList;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @ApiModel(value = "상품 기본 스펙")
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends BasicSpec {
        @JsonIgnore
        private String productCode;     // 파트너 코드
        @JsonIgnore
        private String userId;          // 사용자 아이디

        // JSON -> BasicSpecDto
        public static BasicSpecDto.ReqBody jsonToBasicSpecDto(JSONObject object) {
            if (ObjectUtils.isEmpty(object.getJSONObject("ProductBasicSpecification"))) return null;
            // 탑승자 연령
            List<String> ageList = new ArrayList<>();
            if (!ObjectUtils.isEmpty(object.getJSONArray("ProductEmbarkAge"))) {
                JSONArray jsonArray = object.getJSONArray("ProductEmbarkAge");
                for (int i = 0; i < jsonArray.length(); i++)
                    ageList.add(jsonArray.getJSONObject(i).getString("EmbarkAge"));
            }

            return ReqBody.builder()
                    .productCode(object.getJSONObject("ProductMain").getString("ProductCode"))
                    .userId(object.getJSONObject("ProductMain").getString("UpdateUserId"))
                    .salesCategoryCode(object.getJSONObject("ProductBasicSpecification").getString("SalesCategoryCode"))
                    .drivetrainTypeCode(object.getJSONObject("ProductBasicSpecification").getString("DrivetrainTypeCode"))
                    .frameMaterialCode(object.getJSONObject("ProductBasicSpecification").optString("FrameMaterialCode"))
                    .frameSizeCode(object.getJSONObject("ProductBasicSpecification").getString("FrameSizeCode"))
                    .brakeTypeCode(object.getJSONObject("ProductBasicSpecification").getString("BrakeTypeCode"))
                    .purposeThemeCode(object.getJSONObject("ProductBasicSpecification").getString("PurposeThemeCode"))
                    .wheelSizeCode(object.getJSONObject("ProductBasicSpecification").getString("WheelSizeCode"))
                    .suspensionTypeCode(object.getJSONObject("ProductBasicSpecification").getString("SuspensionTypeCode"))
                    .minHeightPerson(object.getJSONObject("ProductBasicSpecification").getString("MinimumHeightPerson"))
                    .maxHeightPerson(object.getJSONObject("ProductBasicSpecification").getString("MaximumHeightPerson"))
                    .bikeWeight(object.getJSONObject("ProductBasicSpecification").getString("BikeTare"))
                    .ageList(ageList)
                    .build();
        }
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    public static class ResBody extends BasicSpec {}
}
