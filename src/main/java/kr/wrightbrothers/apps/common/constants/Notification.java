package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Notification {
    AUTH_PHONE("", "SMS", "[라이트브라더스] 인증번호 #{authCode} 를 입력해 주세요.", "휴대폰번호 인증"),
    NULL("","", "", "");

    private final String messageId;
    private final String messageType;
    private final String messageText;
    private final String desc;

    Notification(String messageId, String messageType, String messageText, String desc) {
        this.messageId = messageId;
        this.messageType = messageType;
        this.messageText = messageText;
        this.desc = desc;
    }
}
