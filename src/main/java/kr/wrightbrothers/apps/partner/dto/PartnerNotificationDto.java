package kr.wrightbrothers.apps.partner.dto;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class PartnerNotificationDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "파트너 알림 수신 정보")
    public static class PartnerNotification {
        private String partnerCode;
    }

    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "파트너 알림 수신 정보 응답 데이터")
    public static class ResBody {
        private String notificationPhone;
    }
}
