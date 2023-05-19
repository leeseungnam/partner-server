package kr.wrightbrothers.apps.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class RentalDto {

    @Getter @Setter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Rental {
        /** 12개월기준가 */
        private String twlvBseAmt;
        /** 24개월기준가 */
        private String twfoBseAmt;
        /** 36개월기준가 */
        private String thsiBseAmt;
        /** 48개월기준가 */
        private String foeiBseAmt;
        /** 12개월렌탈료 */
        private String twlvMonAmt;
        /** 24개월렌탈료 */
        private String twfoMonAmt;
        /** 36개월렌탈료 */
        private String thsiMonAmt;
        /** 48개월렌탈료 */
        private String foeiMonAmt;
        /** 12개월 렌탈 반납여부 */
        private String twlvRtnFlg;
        /** 24개월 렌탈 반납여부 */
        private String twfoRtnFlg;
        /** 36개월 렌탈 반납여부 */
        private String thsiRtnFlg;
        /** 48개월 렌탈 반납여부 */
        private String foeiRtnFlg;
    }

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class ReqBody extends Rental {
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
    public static class ResBody extends Rental {}

}
