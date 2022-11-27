package kr.wrightbrothers.apps.queue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.batch.dto.UserTargetDto;
import kr.wrightbrothers.apps.batch.service.BatchService;
import kr.wrightbrothers.apps.common.constants.Email;
import kr.wrightbrothers.apps.common.constants.Partner;
import kr.wrightbrothers.apps.email.service.EmailService;
import kr.wrightbrothers.apps.partner.dto.*;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.apps.queue.dto.PartnerReceiveDto;
import kr.wrightbrothers.apps.queue.dto.PartnerSendDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerQueueService {

    private final WBCommonDao dao;
    private final PartnerService partnerService;
    private final BatchService batchService;
    private final EmailService emailService;
    private final String namespace = "kr.wrightbrothers.apps.partner.query.Partner.";

    public PartnerSendDto findPartnerSnsData(String partnerCode,
                                             String contractCode) {
        return PartnerSendDto.builder()
                .registerId(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .partnerCode(partnerCode)
                .contractCode(contractCode)
                .partner(Optional.of((PartnerSNSDto) dao.selectOne(namespace + "findPartnerSNS", partnerCode)).orElse(new PartnerSNSDto()))
                .partnerContract(Optional.of((PartnerContractSNSDto) dao.selectOne(namespace + "findPartnerContractSNS", partnerCode)).orElse(new PartnerContractSNSDto()))
                .build();
    }
    public PartnerInsertDto updatePartnerSnsData(JSONObject body, boolean isUpdateContractDay) throws JsonProcessingException {

        // body convert
        PartnerInsertDto paramDto = convertPartnerInsertDto(body);

        log.info("[updatePartnerSnsData]::paramDto={}",paramDto.toString());
        partnerService.updatePartnerAll(paramDto);

        log.info("[updatePartnerSnsData]::isUpdateContractDay={}", isUpdateContractDay);
        log.info("[updatePartnerSnsData]::ContractDay={}", paramDto.getPartnerContract().getContractDay());
        log.info("[updatePartnerSnsData]::ContractStatus={}", paramDto.getPartnerContract().getContractStatus());

        if(isUpdateContractDay && (Partner.Contract.Status.COMPLETE.getCode().equals(paramDto.getPartnerContract().getContractStatus()))) {
            if(!ObjectUtils.isEmpty(paramDto.getPartnerContract().getContractDay())) {
                //  계약 시작일 : 계약일
                paramDto.getPartnerContract().setContractStartDay(LocalDate.parse(paramDto.getPartnerContract().getContractDay()).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                //  계약 종료일 : 계약일 해당 년도 말일
                paramDto.getPartnerContract().setContractEndDay(LocalDateTime.parse(paramDto.getPartnerContract().getContractDay())
                        .with(TemporalAdjusters.lastDayOfYear())
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd")));

                log.info("[updatePartnerSnsData]::updateContractDay");
                partnerService.updateContractDay(paramDto.getPartnerContract());
            }

        }
        // 심사결과 이메일 전송
        Email email = Email.NULL;

        if(Partner.Contract.Status.REJECT.getCode().equals(paramDto.getPartnerContract().getContractStatus())) {
            email = Email.REJECT_CONTRACT;
        } else if(Partner.Contract.Status.COMPLETE.getCode().equals(paramDto.getPartnerContract().getContractStatus())) {
            email = Email.COMPLETE_CONTRACT;
        }
        List<UserTargetDto> userTargetDtoList = batchService.findPartnerMailByPartnerCode(paramDto.getPartner().getPartnerCode());
        emailService.sendMailPartnerContract(userTargetDtoList, email);

        return paramDto;
    }

    private PartnerInsertDto convertPartnerInsertDto(JSONObject body) throws JsonProcessingException {


        PartnerReceiveDto receiveDto = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .readValue(body.toString(), PartnerReceiveDto.class);

        PartnerInsertDto returnDto = PartnerInsertDto.builder()
                .partner(PartnerDto.ReqBody.builder().build())
                .partnerContract(PartnerContractDto.ReqBody.builder().build())
                .partnerReject(PartnerRejectDto.Param.builder().build())
                .build();

        log.info("[convertPartnerInsertDto]::receiveDto.getPartner()={}",receiveDto.getPartner().toString());
        log.info("[convertPartnerInsertDto]::receiveDto.getPartnerContract()={}",receiveDto.getPartnerContract().toString());

        BeanUtils.copyProperties(receiveDto.getPartner(), returnDto.getPartner());
        BeanUtils.copyProperties(receiveDto.getPartnerContract(), returnDto.getPartnerContract());

        log.info("[convertPartnerInsertDto]::copyProperties done");

        // 심사 변경, 판매자 정보 변경 중 심사 변경 일 경우
        // 파트너 상태, 계약 상태 모두 어드민에서 넘어온 값으로. -> 계약 코드는 어드민에서 처리 안함. requestStatus로 contractStatus Set
        if(!ObjectUtils.isEmpty(receiveDto.getRequestStatus())) {
            if("S01".equals(receiveDto.getRequestStatus())) {
                log.info("[convertPartnerInsertDto]::심사 승인");
//                returnDto.getPartner().changePartnerStatus(Partner.Status.RUN.getCode());
            } else if("S02".equals(receiveDto.getRequestStatus())) {
                log.info("[convertPartnerInsertDto]::심사 반려");
//                returnDto.getPartner().changePartnerStatus(Partner.Status.STOP.getCode());
                returnDto.getPartnerContract().changeContractStatus(Partner.Contract.Status.REJECT.getCode());

                returnDto.setPartnerReject(PartnerRejectDto.Param.builder()
                        .partnerCode(receiveDto.getPartnerCode())
                        .contractCode(receiveDto.getContractCode())
                        .contractStatus(Partner.Contract.Status.REJECT.getCode())
                        .rejectComment(receiveDto.getRejectReason())
                        .build());
            } else {
                log.info("[convertPartnerInsertDto]::Not Support RequestStatus");
            }
        }
        log.info("[convertPartnerInsertDto]::심사 처리 완료");
        returnDto.setAopUserId(receiveDto.getRegisterId());
        log.info("[convertPartnerInsertDto]::registerId={}",receiveDto.getRegisterId());
        log.info("[convertPartnerInsertDto]::returnDto={}",returnDto.toString());
        return returnDto;

    }
}
