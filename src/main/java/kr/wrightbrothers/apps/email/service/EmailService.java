package kr.wrightbrothers.apps.email.service;

import kr.wrightbrothers.apps.common.constants.Email;
import kr.wrightbrothers.apps.common.util.AwsSesUtil;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final AwsSesUtil awsSesUtil;

    public SingleEmailDto.ResBody singleSendEmail(SingleEmailDto.ReqBody paramDto) {

        Email email = Email.valueOfCode(paramDto.getEmailType());

        String authCode = paramDto.getAuthCode();
        String subject = email.getTitle();
        String template = email.getTemplate();
        Context context = new Context();

        context.setVariable("code", authCode);

        awsSesUtil.singleSend(paramDto.getUserId(), subject, template, context);

        return SingleEmailDto.ResBody.builder()
                .userId(paramDto.getUserId())
                .authCode(paramDto.getAuthCode())
                .build();

    }
}
