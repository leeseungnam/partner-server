package kr.wrightbrothers.apps.partner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PartnerContractDto {
    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartnerContract {

        @ApiModelProperty(value = "입점 담당자명", required = true)
        @NotBlank(message = "입점 담당자명")
        @Size(min = 2, max = 20, message = "입점 담당자명")
        private String contractManagerName;

        @ApiModelProperty(value = "입점 담당자 연락처", required = true)
        @NotBlank(message = "입점 담당자 연락처")
        @Size(min = 2, max = 20, message = "입점 담당자 연락처")
        private String contractManagerPhone;

        @ApiModelProperty(value = "세금계산서 이메일", required = true)
        @NotBlank(message = "세금계산서 이메일")
        private String taxBillEmail;

        @ApiModelProperty(value = "은행 코드 공통코드:000046(04:국민은행,02:산업은행,23:SC제일은행,71:우체국,89:케이뱅크,03:기업은행,20:우리은행,39:경남은행,45:새마을금고,37:전북은행"+
                ",11:농협은행,27:씨티은행,34:광주은행,32:부산은행,07:수협은행,35:제주은행,88:신한은행,81:KEB하나은행,31:대구은행,48:신협중앙회,90:카카오뱅크,92:토스뱅크)", required = true)
        @NotBlank(message = "은행 코드")
        @Size(min = 2, max = 20, message = "은행 코드")
        private String bankCode;

        @ApiModelProperty(value = "정산 계좌번호", required = true)
        @NotBlank(message = "정산 계좌번호")
        @Size(min = 2, max = 20, message = "정산 계좌번호")
        private String accountNo;

        @ApiModelProperty(value = "예금주", required = true)
        @NotBlank(message = "예금주")
        @Size(min = 2, max = 20, message = "예금주")
        private String accountHolder;

        @ApiModelProperty(value = "계약일자(YYYYMMDD)")
        private String contractDay;

        @ApiModelProperty(value = "계약기간 시작일(YYYYMMDD)")
        private String contractStartDay;

        @ApiModelProperty(value = "계약기간 종료일(YYYYMMDD)")
        private String contractEndDay;

        @ApiModelProperty(value = "계약서 파일 번호")
        private String contractFileNo;
    }

    @ApiModel(value = "파트너 계약사항 정보")
    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody extends PartnerContract{
        @ApiModelProperty(value = "파트너 코드")
        @JsonIgnore
        private String partnerCode;

        @ApiModelProperty(value = "계약진행 상태 공통코드:000087(C01:심사중, C02:심사승인, C03:심사반려, C04:계약갱신, C05:계약종료, C06:계약철회), C07:계약/정책 위반)")
        private String contractStatus;

        @ApiModelProperty(value = "계약 코드")
        @JsonIgnore
        private String contractCode;

        @ApiModelProperty(value = "작성자 아이디")
        @JsonIgnore
        private String userId;

        public void changeContractCode(String contractCode) {
            this.contractCode = contractCode;
        }
        public void changeUserId(String userId) {
            this.userId = userId;
        }
        public void changeContractStatus(String contractStatus) {
            this.contractStatus = contractStatus;
        }
        public void changePartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }
    }

    @ApiModel(value = "파트너 계약사항 정보")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResBody extends PartnerContract{

        @ApiModelProperty(value = "계약 코드")
        private String contractCode;

        @ApiModelProperty(value = "계약진행 상태 공통코드:000087(C01:심사중, C02:심사승인, C03:심사반려, C04:계약갱신, C05:계약종료, C06:계약철회), C07:계약/정책 위반)")
        private String contractStatus;

        @ApiModelProperty(value = "파트너 코드")
        private String partnerCode;

        @ApiModelProperty(value = "계약진행 상태 공통코드:000087(C01:심사중, C02:심사승인, C03:심사반려, C04:계약갱신, C05:계약종료, C06:계약철회), C07:계약/정책 위반)")
        private String contractStatusName;

        @ApiModelProperty(value = "은행 코드명")
        private String bankCodeName;

        @ApiModelProperty(value = "계약서 파일명")
        private String contractFileName;

        public void changeContractFileName(String contractFileName) {
            this.contractFileName = contractFileName;
        }

        public void changeContractStatusName(String contractStatusName) {
            this.contractStatusName = contractStatusName;
        }
        public void changeBankCodeName(String bankCodeName) {
            this.bankCodeName = bankCodeName;
        }
    }
}
