package kr.wrightbrothers.apps.partner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

public class PartnerUpdateDto {

    @ApiModel(value = "파트너 수정 정보")
    @Getter
    @Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody {
        @ApiModelProperty(value = "파트너 코드")
        @JsonIgnore
        private String partnerCode;

        @ApiModelProperty(value = "계약 코드")
        @JsonIgnore
        private String contractCode;

        @ApiModelProperty(value = "계약 담당자명")
        @NotBlank(message = "계약 담당자명")
        private String contractManagerName;

        @ApiModelProperty(value = "계약 담당자 번호")
        @NotBlank(message = "계약 담당자 번호")
        private String contractManagerPhone;

        @ApiModelProperty(value = "파트너 알림 수신 정보")
        private String [] notificationPhoneList;

        @ApiModelProperty(value = "파트너 알림 수신 정보")
        @JsonIgnore
        private String userId;

        public void changeUserId(String userId) {
            this.userId = userId;
        }
        public void changePartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }
        public void changeContractCode(String contractCode) {
            this.contractCode = contractCode;
        }
    }

    public static class Param {

        @Getter
        @Jacksonized
        @SuperBuilder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Contract {
            private String partnerCode;
            private String contractCode;
            private String contractManagerName;
            private String contractManagerPhone;
            private String accountNo;
            private String accountHolder;
            private String bankCode;
            private String taxBillEmail;
            private String userId;
            private String contractStatus;
            private String contractFileNo;

            public void changeContractStatus(String contractStatus) {
                this.contractStatus = contractStatus;
            }
        }
        @Getter
        @Jacksonized
        @SuperBuilder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Notification {
            private String partnerCode;
            private String [] notificationPhoneList;
            private String userId;
        }
    }
}
