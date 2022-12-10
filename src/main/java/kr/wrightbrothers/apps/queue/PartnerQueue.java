package kr.wrightbrothers.apps.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.batch.dto.UserTargetDto;
import kr.wrightbrothers.apps.batch.service.BatchService;
import kr.wrightbrothers.apps.common.constants.Email;
import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.constants.Partner;
import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.common.constants.DocumentSNS;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.email.service.EmailService;
import kr.wrightbrothers.apps.partner.dto.PartnerInsertDto;
import kr.wrightbrothers.apps.partner.service.PartnerService;
import kr.wrightbrothers.apps.queue.service.PartnerQueueService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO.Header;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerQueue extends WBSQS {

    @Value("${cloud.aws.sns.partner}")
    private String topic;
    @Value("${cloud.aws.sqs.partner}")
    private String queueName;

    private final WBAwsSns sender;
    private final BatchService batchService;
    private final EmailService emailService;
    private final PartnerService partnerService;
    private final PartnerQueueService partnerQueueService;
    private final NotificationQueue notificationQueue;

    /**
     * 입점몰 API -> ADMIN 2.0 API
     * AWS SNS Message Queue 발송 처리
     *
     * @param documentSNS 문서 명
     * @param partnerCode 파트너 코드
     * @param contractCode  계약 코드
     * @param transactionType 전송 타입 I/U
     */
    public void sendToAdmin(DocumentSNS documentSNS,
                            String partnerCode,
                            String contractCode,
                            String transactionType) {
        try {
            log.info("Partner SNS Sender Start");
            // AWS SNS 전송
            sender.send(
                    topic,
                    Header.builder()
                            .docuNm(documentSNS.getName())
                            .trsctnTp(transactionType)
                            .build(),
                    // 상품 SNS 전송 데이터
                    partnerQueueService.findPartnerSnsData(partnerCode, contractCode)
            );
            log.info("Partner SNS Sender END");
        } catch (Exception e) {
            log.error("Partner SNS Sender Error. {}", e.getMessage());
        }
    }

    /**
     * ADMIN 2.0 API -> 입점몰 API
     * AWS SQS Message Queue 수신 처리
     *
     * @param message SQS 스토어 수신 데이터
     */
    @SqsListener(value = "${cloud.aws.sqs.partner}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void receiveFromAdmin(String message) {
        WBSnsDTO snsDto = null;

        log.info("system id login");
        UserPrincipal principal = new UserPrincipal("super@wrightbrothers.kr"
                , ""
                , List.of(new SimpleGrantedAuthority(User.Auth.SUPER.getType()))
                , UserAuthDto.builder().authCode(User.Auth.SUPER.getType()).partnerCode("").build()
        );
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities()));

        try {
            initMessage(message, queueName);
            snsDto = getSqsMessage(WBSnsDTO.class);
            Header header = snsDto.getHeader();

            JSONParser parser = new JSONParser();
            JSONObject body = (JSONObject) parser.parse(new ObjectMapper().writeValueAsString(snsDto.getBody()));

            log.info("[receiveFromAdmin]::Partner");
            log.info("[receiveFromAdmin]::Partner, docName={}",header.getDocuNm());

            // notification data send
            boolean isSendNoti = false;
            String partnerCode = "";
            String [] templateValue = new String[0];
            Notification notification = Notification.NULL;

            boolean isSendEmail = false;
            Email email = Email.NULL;
            Context context = null;

            switch (DocumentSNS.of(header.getDocuNm())){
                case RESULT_INSPECTION_PARTNER:
                    if (PartnerKey.TransactionType.Update.equals(snsDto.getHeader().getTrsctnTp())) {
                        log.info("[receiveFromAdmin]::RESULT_INSPECTION_PARTNER={}",snsDto.getBody());

                        PartnerInsertDto partnerDto = partnerQueueService.updatePartnerSnsData(body, true);
                        partnerCode = partnerDto.getPartner().getPartnerCode();
                        ackMessage(snsDto);

                        context = new Context();
                        context.setVariable("partnerName", partnerDto.getPartner().getPartnerName());

                        // send notification proc
                        log.info("[receiveFromAdmin]::Send ::requestStatus={}",body.get("requestStatus").toString());
                        if("S01".equals(body.get("requestStatus").toString())) {
                            log.info("[receiveFromAdmin]::심사승인");
                            isSendNoti = true;
                            notification = Notification.CONTRACT_COMPLETE;
                            // templateValue(심사승인) : 스토어명, 계약 시작일, 계약 종료일
                            templateValue = new String[]{partnerDto.getPartner().getPartnerName(), partnerDto.getPartnerContract().getContractStartDay(), partnerDto.getPartnerContract().getContractEndDay()};

                            isSendEmail = true;
                            email = Email.COMPLETE_CONTRACT;
                        } else if("S02".equals(body.get("requestStatus").toString())) {
                            log.info("[receiveFromAdmin]::심사반려");
                            isSendNoti = true;
                            notification = Notification.REJECT_STORE;
                            // templateValue(심사반려) : 스토어명
                            templateValue = new String[]{partnerDto.getPartner().getPartnerName()};

                            isSendEmail = true;
                            email = Email.REJECT_CONTRACT;
                            context.setVariable("rejectComment", partnerDto.getPartnerReject().getRejectComment());
                        } else {
                            log.info("[receiveFromAdmin]::Send Fail::requestStatus={}",body.get("requestStatus").toString());
                        }
                    }
                    break;
                case UPDATE_PARTNER:
                    if(PartnerKey.TransactionType.Update.equals(snsDto.getHeader().getTrsctnTp())) {
                        log.info("[receiveFromAdmin]::UPDATE_PARTNER={}",snsDto.getBody());

                        PartnerInsertDto partnerDto = partnerQueueService.updatePartnerSnsData(body, false);
                        partnerCode = partnerDto.getPartner().getPartnerCode();
                        ackMessage(snsDto);

                        context = new Context();
                        context.setVariable("partnerName", partnerDto.getPartner().getPartnerName());

                        // send notification proc
                        if(Partner.Status.STOP.getCode().equals(partnerDto.getPartner().getPartnerStatus())) {
                            if(Partner.Contract.Status.COMPLETE.getCode().equals(partnerDto.getPartnerContract().getContractStatus())) {
                                // 계약위반
                                isSendNoti = true;
                                notification = Notification.VIOLATION;
                                // templateValue(계약위반) : 스토어명
                                templateValue = new String[]{partnerDto.getPartner().getPartnerName()};
                            }else if(Partner.Contract.Status.WITHDRAWAL.getCode().equals(partnerDto.getPartnerContract().getContractStatus())) {
                                // 계약종료
                                isSendNoti = true;
                                notification = Notification.CONTRACT_END;
                                // templateValue(계약종료) : 스토어명
                                templateValue = new String[]{partnerDto.getPartner().getPartnerName()};

                                //  계약종료(즉시) 에 대해서는 이메일 발송 안하는 걸로 변경.
                                //  계약종료 및 갱신 안내 -> 어드민 배치로 처리 (종료 30일 전 메일발송)
                                isSendEmail = false;
                                email = Email.END_CONTRACT;
                            }
                        }
                    }
                    break;
            }
            //  Send Email
            log.info("[receiveFromAdmin]::isSendEmail={}", isSendEmail);
            if(isSendEmail){
                List<UserTargetDto> userTargetDtoList = batchService.findPartnerMailByPartnerCode(partnerCode);
                emailService.sendMailPartnerContract(userTargetDtoList, email, context);
            }
            //  Send Notification
            log.info("[receiveFromAdmin]::isSendNoti={}", isSendNoti);
            if(isSendNoti) {
                List<String> phoneList = partnerService.findPartnerNotiTargetByPartnerCode(partnerCode);
                for(String to : phoneList) {
                    log.info("[receiveFromAdmin::sendPushToAdmin]::to={}", to);
                    notificationQueue.sendPushToAdmin(DocumentSNS.NOTI_KAKAO_SINGLE
                            , notification
                            , to
                            , templateValue
                    );
                }
            }
        } catch (Exception e) {
            log.error("Partner SQS Receive Error. {}", e.getMessage());
            ackMessage(snsDto, e);
        }

    }
}
