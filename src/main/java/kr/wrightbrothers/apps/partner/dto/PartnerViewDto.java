package kr.wrightbrothers.apps.partner.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PartnerViewDto {

    @Getter
    @Builder
    public static class Param {
        private String partnerCode;
        private String contractCode;
        private String authCode;
    }

    @Getter
    @Builder
    public static class ResBody {
        private PartnerDto.ResBody partner;
        private PartnerContractDto.ResBody partnerContract;
        private List<PartnerOperatorDto.ResBody> partnerOperator;
        private List<PartnerNotificationDto.ResBody> partnerNotification;
        private List<PartnerRejectDto.ResBody> partnerReject;
    }
}
