package kr.wrightbrothers.apps.partner.dto;

import kr.wrightbrothers.apps.user.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PartnerViewDto {
    @Getter
    @Builder
    public static class Param {
        private String partnerCode;
        private String authCode;
    }

    @Getter
    @Builder
    public static class ResBody {
        private PartnerDto.ResBody partner;
        private PartnerContractDto.ResBody partnerContract;
        private List<UserDto> partnerOperator;
        private List<PartnerNotificationDto.ResBody> partnerNotification;
        private List<PartnerRejectDto.ResBody> partnerReject;
    }
}
