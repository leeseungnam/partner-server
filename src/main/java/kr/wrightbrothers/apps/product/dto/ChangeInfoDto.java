package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.type.ProductLogCode;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class ChangeInfoDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeInfo {
        /** 상품 로드 */
        private String productLog;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends ChangeInfo {
        /** 상품 상태 코드 */
        private String productStatusCode;

        /** 상품 로그 코드 */
        private String productLogCode;

        /** 상품 코드 */
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
    @AllArgsConstructor
    public static class ResBody extends ChangeInfo {
        /** 상품 상태 코드 */
        private String productStatusCode;

        /** 상품 로그 코드 */
        private String productLogCode;

        /** 작성자 */
        private String createUserName;

        /** 작성일시 */
        private String createDate;

        // 상품 상태 ENUM 처리
        public void setProductStatusCode(String productStatusCode) {
            this.productStatusCode = ProductStatusCode.of(productStatusCode).getName();
        }
        // 로드 상태 ENUM 처리
        public void setProductLogCode(String productLogCode) {
            this.productLogCode = ProductLogCode.of(productLogCode).getName();
        }
    }
}
