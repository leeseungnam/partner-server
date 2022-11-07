package kr.wrightbrothers.apps.queue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.constants.Partner;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerQueueService {

    private final WBCommonDao dao;
    private final PartnerService partnerService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
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
                .registerId(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .partnerCode(partnerCode)
                .contractCode(contractCode)
                .partner(Optional.of((PartnerSNSDto) dao.selectOne(namespace + "findPartnerSNS", partnerCode)).orElse(new PartnerSNSDto()))
                .partnerContract(Optional.of((PartnerContractSNSDto) dao.selectOne(namespace + "findPartnerContractSNS", contractCode)).orElse(new PartnerContractSNSDto()))
                .build();
    }

    public void updatePartnerSnsData(JSONObject body) throws JsonProcessingException {

        // system 로그인 처리
/*
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken("super@wrightbrothers.kr", "1q2w3e4r5t");

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("system id login");
*/

        log.info("[updatePartnerSnsData]::RESULT_INSPECTION_PARTNER");

        // body convert
        PartnerInsertDto paramDto = convertPartnerInsertDto(body);

        log.info("[updatePartnerSnsData]::paramDto={}",paramDto.toString());
        //  [todo] beforeAop 처리 추가 필요.
        partnerService.updatePartnerAll(paramDto);
    }

    private PartnerInsertDto convertPartnerInsertDto(JSONObject body) throws JsonProcessingException {


        PartnerReceiveDto receiveDto = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .readValue(body.toString(), PartnerReceiveDto.class);

        PartnerInsertDto returnDto = PartnerInsertDto.builder()
                .partner(PartnerDto.ReqBody.builder().build())
                .partnerContract(PartnerContractDto.ReqBody.builder().build())
                .build();

        log.info("[convertPartnerInsertDto]::receiveDto.getPartner()={}",receiveDto.getPartner().toString());
        log.info("[convertPartnerInsertDto]::receiveDto.getPartnerContract()={}",receiveDto.getPartnerContract().toString());

        BeanUtils.copyProperties(receiveDto.getPartner(), returnDto.getPartner());
        BeanUtils.copyProperties(receiveDto.getPartnerContract(), returnDto.getPartnerContract());

        if("S01".equals(receiveDto.getRequestStatus())) {
            returnDto.getPartner().changePartnerStatus(Partner.Status.RUN.getCode());
        } else {
            returnDto.getPartner().changePartnerStatus(Partner.Status.STOP.getCode());
            returnDto.getPartnerContract().changeContractStatus(Partner.Contract.Status.REJECT.getCode());

            returnDto.setPartnerReject(PartnerRejectDto.Param.builder()
                            .partnerCode(receiveDto.getPartnerCode())
                            .contractCode(receiveDto.getContractCode())
                            .contractStatus(Partner.Contract.Status.REJECT.getCode())
                            .rejectComment(receiveDto.getRejectReason())
                    .build());
        }
        returnDto.setAopUserId(receiveDto.getRegisterId());

        return returnDto;

    }
}
