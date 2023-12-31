package kr.wrightbrothers.apps.email.service;

import kr.wrightbrothers.apps.batch.dto.UserTargetDto;
import kr.wrightbrothers.apps.common.constants.Email;
import kr.wrightbrothers.apps.common.util.AwsSesUtil;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final AwsSesUtil awsSesUtil;

    public void sendMailPartnerContract(List<UserTargetDto> targetList, Email email, Context context) {
        log.info("[sendMailPartnerContract]:: ... start");

        log.info("[sendMailPartnerContract]:: Send Mail Target={}", targetList.toString());

        for (UserTargetDto targetDto : targetList) {
            // 메일 발송 대상자
            if(ObjectUtils.isEmpty(context)) {
                context = new Context();
            }
            context.setVariable("userName", targetDto.getUserName());

            log.info("[sendMailPartnerContract]::target={}",targetDto.getUserName());
            // 메일발송
            awsSesUtil.singleSend(
                    email.getTitle(),
                    email.getTemplate(),
                    context,
                    targetDto.getReceiver()
            );
        }
        log.info("[sendMailPartnerContract]:: ... end");
    }
    public SingleEmailDto.ResBody singleSendEmail(SingleEmailDto.ReqBody paramDto) {

        Email email = Email.valueOfCode(paramDto.getEmailType());
        if(ObjectUtils.isEmpty(email)) throw new WBBusinessException(ErrorCode.INTERNAL_SERVER.getErrCode());

        String authCode = paramDto.getAuthCode();
        String subject = email.getTitle();
        String template = email.getTemplate();

        Context context = new Context();
        context.setVariable("code", authCode);

        if(paramDto.getEmailType().equals(Email.PASSWORD.getCode()) || paramDto.getEmailType().equals(Email.INVITE_OPERATOR.getCode()))
            context.setVariable("userName", paramDto.getUserName());

        log.info("[singleSendEmail]::type={}",email.getCode());
        log.info("[singleSendEmail]::code={}",authCode);
        log.info("[singleSendEmail]::subject={}",subject);
        log.info("[singleSendEmail]::template={}",template);
        log.info("[singleSendEmail]::to={}", paramDto.getUserId());

        try {

            awsSesUtil.singleSend(subject, template, context, paramDto.getUserId());

            return SingleEmailDto.ResBody.builder()
                    .userId(paramDto.getUserId())
                    .authCode(paramDto.getAuthCode())
                    .build();

        } catch (Exception e) {
            log.info("[singleSendEmail]::Exception={}",e.getClass());
            throw new WBBusinessException(ErrorCode.INTERNAL_SERVER.getErrCode());
        }
    }
}
