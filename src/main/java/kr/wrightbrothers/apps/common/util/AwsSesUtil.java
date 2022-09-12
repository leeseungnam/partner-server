package kr.wrightbrothers.apps.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailRequest.Builder;

@Component
@RequiredArgsConstructor
public class AwsSesUtil {

    private final SesClient sesAsyncClient;
    private final SpringTemplateEngine springTemplateEngine;
    @Value("${system.mail.sender}")
    private String sender;

    /**
     * 메일 발송 처리
     *
     * @param to 수신자
     * @param subject 메일 발송 제목
     * @param template 메일 템플릿
     * @param context Context(템플릿 변수 관련 처리시 필요)
     */
    public void singleSend(String to,
                           String subject,
                           String template,
                           Context context) {
        String html = springTemplateEngine.process(template, context);

        final Builder sendEmailRequestBuilder = SendEmailRequest.builder();
        sendEmailRequestBuilder.destination(Destination.builder().toAddresses(to).build());
        sendEmailRequestBuilder.message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder().html(body -> body.data(html)).build())
                        .build()
                ).source(sender).build();

        sesAsyncClient.sendEmail(sendEmailRequestBuilder.build());
    }
}
