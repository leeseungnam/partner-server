package kr.wrightbrothers.apps.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ChangeInfoListDto {

    @Getter @Builder
    @AllArgsConstructor
    public static class Param {
        /** 파트너 코드 */
        private String partnerCode;

        /** 상품 코드 */
        private String productCode;
    }

    @Getter @Builder
    @AllArgsConstructor
    public static class Response {
        /** 상품 코드 */
        private String productCode;

        /** 상품 이름 */
        private String productName;

        /** 변경 내역 */
        private List<ChangeInfoDto.ResBody> changeHistory;
    }

}
