package kr.wrightbrothers.apps.partner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PartnerContractDto {
    @Getter
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

        @ApiModelProperty(value = "은행 코드", required = true)
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
        @JsonIgnore
        private String contractFileNo;
    }

    @ApiModel(value = "파트너 계약사항 정보")
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReqBody extends PartnerContract{
        @ApiModelProperty(value = "파트너 코드")
        @JsonIgnore
        private String partnerCode;

        @ApiModelProperty(value = "계약진행 상태 공통코드:000087(C01:계약전, C02:계약중, C03:재계약, C04:계약갱신, C05:계약종료, C06:계약철회)")
        @JsonIgnore
        private String contractStatus;

        @ApiModelProperty(value = "계약번호")
        @JsonIgnore
        private String contractNo;

        @ApiModelProperty(value = "작성자 아이디")
        @JsonIgnore
        private String userId;

        public void changeContractNo(String contractNo) {
            this.contractNo = contractNo;
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

        @ApiModelProperty(value = "계약번호")
        private String contractNo;

        @ApiModelProperty(value = "계약진행 상태 공통코드:000087(C01:계약전, C02:계약중, C03:재계약, C04:계약갱신, C05:계약종료, C06:계약철회)")
        private String contractStatus;

        @ApiModelProperty(value = "파트너 코드")
        private String partnerCode;

        @ApiModelProperty(value = "계약진행 상태명 공통코드:000087(C01:계약전, C02:계약중, C03:재계약, C04:계약갱신, C05:계약종료, C06:계약철회)")
        private String contractStatusName;

        @ApiModelProperty(value = "은행 코드명")
        private String bankCodeName;

        public void changeContractStatusName(String contractStatusName) {
            this.contractStatusName = contractStatusName;
        }
        public void changeBankCodeName(String bankCodeName) {
            this.bankCodeName = bankCodeName;
        }
    }
}
