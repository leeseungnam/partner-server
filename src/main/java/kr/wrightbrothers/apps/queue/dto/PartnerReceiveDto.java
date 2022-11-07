package kr.wrightbrothers.apps.queue.dto;

import kr.wrightbrothers.apps.partner.dto.PartnerContractSNSDto;
import kr.wrightbrothers.apps.partner.dto.PartnerSNSDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerReceiveDto {

    private String partnerCode;
    private String contractCode;
    private String registerId;
    private String requestStatus;
    private String rejectReason;

    private PartnerSNSDto partner; // 파트너 정보
    private PartnerContractSNSDto partnerContract; //  파트너 계약정보

}
