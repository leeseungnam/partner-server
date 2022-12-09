package kr.wrightbrothers.apps.temporary.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class TemporaryDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Param {
        /** 파트너 코드 */
        private String partnerCode;

        /** 사용자 아이디 */
        private String userId;

        /** 임시저장 타입 */
        private String storageType;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody {
        /** 임시저장 타입 */
        @NotBlank(message = "임시저장 타입")
        private String storageType;

        /** 임시저장 데이터 */
        @NotBlank(message = "임시저장 데이터")
        private String storageData;

        /** 파트너 코드 */
        @JsonIgnore
        private String partnerCode;

        /** 사용자 아이디 */
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
