package kr.wrightbrothers.apps.temporary.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TemporaryDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Param {
        private String partnerCode;
        private String userId;
        private String storageType;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody {
        private String storageData;
        private String storageType;

        @JsonIgnore
        private String partnerCode;
        @JsonIgnore
        private String userId;

        public void setAopPartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }

        public void setAopUserId(String userId) {
            this.userId = userId;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String storageData;
    }

}
