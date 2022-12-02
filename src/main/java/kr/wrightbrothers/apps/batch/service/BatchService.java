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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
//                                .contractDay(LocalDate.now().toString())    //  계약일은 최초계약일 개념으로 변경X
                                .contractStartDay(LocalDateTime.now()
                                        .with(TemporalAdjusters.firstDayOfYear())
                                        .toLocalDate()
                                        .format(DateTimeFormatter.ofPattern("yyyyMMdd")))    // 배치 실행 년도의 첫 번째 일(1월 1일)
                                .contractEndDay(LocalDateTime.now()
                                        .with(TemporalAdjusters.lastDayOfYear())
                                        .toLocalDate()
                                        .format(DateTimeFormatter.ofPattern("yyyyMMdd")))   // 배치 실행 년도의 마지막 일(12월 31일)
                        .build()
                );
                // 계약 자동 갱신 회원(관리자) 메일 발송
                List<UserTargetDto> userTargetList = findPartnerMailByPartnerCode(targetDto.getPartnerCode());
                emailService.sendMailPartnerContract(userTargetList, Email.RENEWAL_CONTRACT, null);

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
