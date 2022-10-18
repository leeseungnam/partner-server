package kr.wrightbrothers.apps.partner.dto;

import lombok.Builder;
import lombok.Getter;

public class PartnerViewDto {
    @Getter
    @Builder
    public static class Param {
        private String partnerCode;
    }

    @Getter
    @Builder
    public static class ResBody {
        private PartnerDto.ResBody partner;
        private PartnerContractDto.ResBody partnerContract;
    }
}
