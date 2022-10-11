package kr.wrightbrothers.apps.partner.dto;

import lombok.Builder;
import lombok.Getter;

public class PartnerFindDto {
    @Getter
    @Builder
    public static class Param {
        private String businessNo;
        private String businessClassificationCode;
    }

    @Getter
    @Builder
    public static class ResBody {
        private PartnerDto.ResBody partner;
    }
}
