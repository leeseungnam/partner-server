package kr.wrightbrothers.apps.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReturnPartnerDto {

    @Getter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReqBody {
        /** 파트너 코드 */
        private String prnrCd;
        /** 상품코드 */
        private String prdtCd;
    }

    @Getter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        /** 파트너 명 */
        private String prnrNm;
        /** 고객센터 전화번호 */
        private String csPhn;

    }

    @Getter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        /** 반품지 주소 */
        private String rtnAddr;
        /** 반품지 상세주소 */
        private String rtnAddrDtl;

    }

}
