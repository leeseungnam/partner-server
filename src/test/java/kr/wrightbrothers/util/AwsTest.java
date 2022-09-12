package kr.wrightbrothers.util;

import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.util.AwsSesUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;


class AwsTest extends BaseControllerTests {

    @Autowired
    private AwsSesUtil awsSesUtil;

    @DisplayName("SES 메일발송 테스트")
    void SesSendTest() {

        String subject = "메일발송 테스트";

        Context context = new Context();
        context.setVariable("code", 7777);

        awsSesUtil.singleSend("chals@wrightbrothers.kr",
                subject,
                "userMailAuth",
                context);
    }

}
