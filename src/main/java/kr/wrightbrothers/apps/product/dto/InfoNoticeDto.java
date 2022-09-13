package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class InfoNoticeDto {

    @Data
    @Jacksonized
    @SuperBuilder
    public static class InfoNotice {
        private String categoryCode;
        private String modelName;
        private String productSize;
        private String productWeight;
        private String productMaterial;
        private String productComponent;
        private String modelYear;
        private String modelMonth;
        private String productMfr;
        private String detailSpec;
        private String qaStandard;
        private String asPhone;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends InfoNotice {
        @JsonIgnore
        private String partnerCode;         // 파트너 코드
        @JsonIgnore
        private String userId;              // 작성자 아이디
    }

}
