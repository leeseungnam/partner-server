package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Email {
    AUTH("1", "[라이트브라더스] 파트너센터 회원가입 인증", "join_email"),
    PASSWORD("2", "[라이트브라더스] 파트너센터 임시 비밀번호 안내", "join_pw"),

    END("","", "");

    private final String code;
    private final String title;
    private final String template;

    Email(String code, String title, String template) {
        this.code = code;
        this.title = title;
        this.template = template;
    }

    private static final Map<String, Email> CODE_MAP =
            Stream.of(values()).collect(Collectors.toMap(Email::getCode, Function.identity()));

    public static Email valueOfCode(String code) {
        return CODE_MAP.get(code);
    }
}
