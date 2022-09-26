package kr.wrightbrothers.apps.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ChangeInfoListDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Param {
        private String partnerCode;     // 파트너 코드
        private String productCode;     // 상품 코드
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private String productCode;     // 상품 코드
        private String productName;     // 상품 이름
        private List<ChangeInfoDto.ResBody> changeHistory;  // 변경 내역
    }

}
