package kr.wrightbrothers.apps.email.service;

import kr.wrightbrothers.apps.common.constants.Email;
import kr.wrightbrothers.apps.common.util.AwsSesUtil;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final AwsSesUtil awsSesUtil;

    public SingleEmailDto.ResBody singleSendEmail(SingleEmailDto.ReqBody paramDto) {

        Email email = Email.valueOfCode(paramDto.getEmailType());
        if(ObjectUtils.isEmpty(email)) throw new WBBusinessException(ErrorCode.INTERNAL_SERVER.getErrCode());

        String authCode = paramDto.getAuthCode();
        String subject = email.getTitle();
        String template = email.getTemplate();

        Context context = new Context();
        context.setVariable("code", authCode);

        log.info("[singleSendEmail]::authCode={}",authCode);
        log.info("[singleSendEmail]::subject={}",subject);
        log.info("[singleSendEmail]::template={}",template);

        try {

            awsSesUtil.singleSend(paramDto.getUserId(), subject, template, context);

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
