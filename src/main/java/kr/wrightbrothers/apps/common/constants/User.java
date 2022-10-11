package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class User {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Auth {
        SUPER("ROLE_SUPER", "슈퍼 관리자"),
        ADMIN("ROLE_ADMIN", "관리자"),
        MANAGER("ROLE_MANAGER", "운영자"),
        USER("ROLE_USER", "일반 유저")
        ;

        private final String type;
        private final String name;

        Auth(String type, String name) {
            this.type = type;
            this.name = name;
        }

        private static final Map<String, User.Auth> CODE_MAP =
                Stream.of(values()).collect(Collectors.toMap(User.Auth::getType, Function.identity()));

        public static User.Auth valueOfCode(String type) {
            return CODE_MAP.get(type);
        }
    }
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Status {
        COMMON("U01", "정상")
        ;

        private final String code;
        private final String name;

        Status(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, User.Status> CODE_MAP =
                Stream.of(values()).collect(Collectors.toMap(User.Status::getCode, Function.identity()));

        public static User.Status valueOfCode(String code) {
            return CODE_MAP.get(code);
        }
    }

}
