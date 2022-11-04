package kr.wrightbrothers.apps.queue.service;

import kr.wrightbrothers.apps.partner.dto.PartnerContractSNSDto;
import kr.wrightbrothers.apps.partner.dto.PartnerSNSDto;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.apps.queue.dto.PartnerSendDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerQueueService {

    private final WBCommonDao dao;
    private final PartnerService partnerService;
    private final String namespace = "kr.wrightbrothers.apps.partner.query.Partner.";

    // SNS 입점몰 상품 정보 조회
//    public PartnerSendDto findPartnerSnsData(String partnerCode,
//                                             String contractCode) {
//        return PartnerSendDto.builder()
//                .partner(Optional.of((PartnerDto.ResBody) dao.selectOne(namespace + "findPartnerByPartnerCode", partnerCode)).orElse(new PartnerDto.ResBody()))
//                .partnerContract(Optional.of((PartnerContractDto.ResBody) dao.selectOne(namespace + "findPartnerContractByPartnerCode", PartnerViewDto.Param.builder()
//                        .partnerCode(partnerCode)
//                        .contractCode(contractCode)
//                        .build())).orElse(new PartnerContractDto.ResBody()))
//                .build();
//    }
    public PartnerSendDto findPartnerSnsData(String partnerCode,
                                             String contractCode) {
        return PartnerSendDto.builder()
                .partnerCode(partnerCode)
                .contractCode(contractCode)
                .partner(Optional.of((PartnerSNSDto) dao.selectOne(namespace + "findPartnerSNS", partnerCode)).orElse(new PartnerSNSDto()))
                .partnerContract(Optional.of((PartnerContractSNSDto) dao.selectOne(namespace + "findPartnerContractSNS", contractCode)).orElse(new PartnerContractSNSDto()))
                .build();
    }
}
