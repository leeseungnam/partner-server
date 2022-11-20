package kr.wrightbrothers.apps.batch.service;

import kr.wrightbrothers.apps.batch.dto.PartnerTargetDto;
import kr.wrightbrothers.apps.batch.dto.UserTargetDto;
import kr.wrightbrothers.apps.common.constants.Email;
import kr.wrightbrothers.apps.email.service.EmailService;
import kr.wrightbrothers.apps.partner.dto.PartnerContractDto;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {
    private final PartnerService partnerService;
    private final WBCommonDao dao;
    private final EmailService emailService;
    private final String namespace = "kr.wrightbrothers.apps.batch.query.Batch.";

    public void renewalContract() {
        log.info("[renewalContract]::run");

        //  find target PartnerContact
        List<PartnerTargetDto> targetList = dao.selectList(namespace + "findPartnerContractRenewal", null);
        List<PartnerTargetDto> failList = new ArrayList<>();

        for(PartnerTargetDto targetDto : targetList) {
            try {
                // insert logs
                // update contract
                partnerService.updateContractDay(PartnerContractDto.ReqBody.builder()
                                .partnerCode(targetDto.getPartnerCode())
                                .contractCode(targetDto.getContractCode())
                                .contractDay(LocalDate.now().toString())
                                .contractStartDay(LocalDate.now().toString())
                                .contractEndDay(LocalDate.now().plusYears(1).toString())
                        .build()
                );

                // 계약 자동 갱신 회원(관리자) 메일 발송
                List<UserTargetDto> userTargetList = findPartnerMailByPartnerCode(targetDto.getPartnerCode());
                emailService.sendMailPartnerContract(userTargetList, Email.RENEWAL_CONTRACT);

            } catch (Exception e) {
                log.error("[renewalContract]::Error::targetDto={}",targetDto.toString());
                failList.add(targetDto);
                targetList.removeIf(entity -> entity.equals(targetDto));
            }
        }
        log.info("[renewalContract]::success target ... start");
        targetList.forEach(target -> log.info("[renewalContract]::success target={}",target.toString()));
        log.info("[renewalContract]::success target ... end\n");

        log.info("[renewalContract]::success target ... start");
        failList.forEach(target -> log.info("[renewalContract]::fail target={}",target.toString()));
        log.info("[renewalContract]::success target ... end");

        log.info("[renewalContract]::done");
    }

    public List<UserTargetDto> findPartnerMailByPartnerCode(String partnerCode) {
        return dao.selectList(namespace + "findPartnerMailByPartnerCode", partnerCode);
    }
}
