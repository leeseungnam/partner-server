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
        private String productLog;          // 상품 로그
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends ChangeInfo {
        private String productStatusCode;   // 상품 상태 코드
        private String productLogCode;      // 상품 로그 코드
        @JsonIgnore
        private String productCode;         // 상품 코드
        @JsonIgnore
        private String userId;              // 사용자 아이디
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResBody extends ChangeInfo {
        private String productStatusCode;   // 상품 상태 코드
        private String productLogCode;      // 상품 로그 코드
        private String createUserName;      // 작성자
        private String createDate;          // 작성일시

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
