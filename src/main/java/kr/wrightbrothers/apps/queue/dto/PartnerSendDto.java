package kr.wrightbrothers.apps.queue.dto;

import kr.wrightbrothers.apps.partner.dto.PartnerContractSNSDto;
import kr.wrightbrothers.apps.partner.dto.PartnerSNSDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PartnerSendDto {
    /*
    private PartnerDto.ResBody partner; // 파트너 정보
    private PartnerContractDto.ResBody partnerContract; //  파트너 계약정보

    @Builder
    public PartnerSendDto(PartnerDto.ResBody partner, PartnerContractDto.ResBody partnerContract) {
        this.partner = partner;
        this.partnerContract = partnerContract;
    }
    */
    private String partnerCode;
    private String contractCode;
    private String registerId;
    private PartnerSNSDto partner; // 파트너 정보
    private PartnerContractSNSDto partnerContract; //  파트너 계약정보

    @Builder
    public PartnerSendDto(String partnerCode, String contractCode, String registerId, PartnerSNSDto partner, PartnerContractSNSDto partnerContract) {
        this.registerId = registerId;
        this.partnerCode = partnerCode;
        this.contractCode = contractCode;
        this.partner = partner;
        this.partnerContract = partnerContract;
    }

}
