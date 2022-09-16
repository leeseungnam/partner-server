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
        private String partnerCode;
        private String productCode;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private String productCode;
        private String productName;
        private List<ChangeInfoDto.ResBody> changeHistory;
    }

}
