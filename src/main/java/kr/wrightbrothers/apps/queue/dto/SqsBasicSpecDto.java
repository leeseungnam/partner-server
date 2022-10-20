package kr.wrightbrothers.apps.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SqsBasicSpecDto {
    @JsonProperty("SalesCategoryCode")
    private String salesCategoryCode;       // 완차구분 코드

    @JsonProperty("DrivetrainTypeCode")
    private String drivetrainTypeCode;      // 구동계 코드

    @JsonProperty("FrameMaterialCode")
    private String frameMaterialCode;       // 프레임 소재 코드

    @JsonProperty("FrameSizeCode")
    private String frameSizeCode;           // 프레임 사이즈 코드

    @JsonProperty("BrakeTypeCode")
    private String brakeTypeCode;           // 브레이크 종류 코드

    @JsonProperty("PurposeThemeCode")
    private String purposeThemeCode;        // 용도 테마 코드

    @JsonProperty("WheelSizeCode")
    private String wheelSizeCode;           // 휠사이즈 코드

    @JsonProperty("SuspensionTypeCode")
    private String suspensionTypeCode;      // 서스펜션 코드

    @JsonProperty("MinimumHeightPerson")
    private String minHeightPerson;         // 탑승자 최소 키

    @JsonProperty("MaximumHeightPerson")
    private String maxHeightPerson;         // 탑승자 최대 키

    @JsonProperty("BikeTare")
    private String bikeWeight;              // 자전거 무게

    private List<String> ageList;           // 탑승자 연령
}
