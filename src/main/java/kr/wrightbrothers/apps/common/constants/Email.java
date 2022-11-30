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
    COMPLETE_PRODUCT("3", "[라이트브라더스] 파트너센터 상품 검수 완료 안내", "complete_product"),
    REJECT_PRODUCT("4", "[라이트브라더스] 파트너센터 상품 검수 반려 안내", "reject_product"),
    INVITE_OPERATOR("5", "[라이트브라더스] 파트너센터 운영자 초대", "invite_operator"),
    RENEWAL_CONTRACT("6", "[라이트브라더스] 파트너센터 계약 갱신 안내", "renewal_contract"),
    REJECT_CONTRACT("7", "[라이트브라더스] 파트너센터 스토어 심사 반려 안내", "reject_contract"),
    COMPLETE_CONTRACT("8", "[라이트브라더스] 파트너센터 스토어 심사 완료 안내", "complete_contract"),
    END_CONTRACT("9", "[라이트브라더스] 파트너센터 계약 종료 안내", "end_contract"),

    NULL("","", "");

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
