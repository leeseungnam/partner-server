package kr.wrightbrothers.util;

import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.util.AwsSesUtil;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.apps.email.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;

import java.util.Collections;
import java.util.List;


class AwsTest extends BaseControllerTests {

    @Autowired
    private AwsSesUtil awsSesUtil;
    @Autowired
    private EmailService emailService;

    @DisplayName("SES 메일발송 테스트")
    void SesSendTest() {

        emailService.singleSendEmail(SingleEmailDto.ReqBody.builder()
                        .authCode("3333")
                        .emailType("1")
                        .userId("chals@wrightbrothers.kr")
                .build());


//        String subject = "메일발송 테스트";
//
//        Context context = new Context();
//        context.setVariable("code", 7777);
//
//        awsSesUtil.singleSend(
//                subject,
//                "userMailAuth",
//                context,
//                "chals@wrightbrothers.kr");
    }

}
