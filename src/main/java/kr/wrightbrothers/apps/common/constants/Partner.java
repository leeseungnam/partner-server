package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Partner {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Status {
        REQUEST("P01", "심사요청"),
        COMPLETE_SUCESS("P02", "심사완료(통과)"),
        COMPLETE_FAIL("P03", "심사완료(반려)")
        ;

        private final String code;
        private final String name;

        Status(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, Partner.Status> CODE_MAP =
                Stream.of(values()).collect(Collectors.toMap(Partner.Status::getCode, Function.identity()));

        public static Partner.Status valueOfCode(String code) {
            return CODE_MAP.get(code);
        }
    }

    public static class Contract {

        @Getter
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        public enum Status {
            EMPTY("C01", "대기"),
            COMPLETE("C02", "계약완료"),
            RENEWAL("C03", "재계약"),
            AUTOMATIC("C04", "계약갱신"),
            WITHDRAWAL("C05", "계약철회")
            ;

            private final String code;
            private final String name;

            Status(String code, String name) {
                this.code = code;
                this.name = name;
            }

            private static final Map<String, Partner.Contract.Status> CODE_MAP =
                    Stream.of(values()).collect(Collectors.toMap(Partner.Contract.Status::getCode, Function.identity()));

            public static Partner.Contract.Status valueOfCode(String code) {
                return CODE_MAP.get(code);
            }
        }
    }
}
