package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.wrightbrothers.apps.common.type.ProductLogCode;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class ChangeInfoDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeInfo {
        private String productLog;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends ChangeInfo {
        private String productStatusCode;
        private String productLogCode;
        @JsonIgnore
        private String productCode;
        @JsonIgnore
        private String userId;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResBody extends ChangeInfo {
        private String productStatusCode;
        private String productLogCode;
        private String createUserName;
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
