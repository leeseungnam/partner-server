package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeliveryConst {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Type {

        PARCEL("D01", "택배"),
        PICKUP("D06", "방문수령"),
        FREIGHT("D07", "화물"),
        NULL("", "");

        private final String type;
        private final String name;

        Type(String type, String name) {
            this.type = type;
            this.name = name;
        }

        private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
                Stream.of(values()).collect(
                        Collectors.toMap(Type::getType, Type::name)
                )
        );

        public static Type of(final String type) {
            if (ObjectUtils.isEmpty(CODE_MAP.get(type)))
                return Type.NULL;

            return Type.valueOf(CODE_MAP.get(type));
        }
    }

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Charge {
        PAY("1", "유료"),
        FREE("2", "무료"),
        TERMS_FREE("3", "조건부 무료"),
        NULL("", "");

        private final String type;
        private final String name;

        Charge(String type, String name) {
            this.type = type;
            this.name = name;
        }

        private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
                Stream.of(values()).collect(
                        Collectors.toMap(Charge::getType, Charge::name)
                )
        );

        public static Charge of(final String type) {
            if (ObjectUtils.isEmpty(CODE_MAP.get(type)))
                return Charge.NULL;

            return Charge.valueOf(CODE_MAP.get(type));
        }

    }

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Status {
        READY_PRODUCT("D01", "상품준비중"),
        START_DELIVERY("D02", "배송중"),
        FINISH_DELIVERY("D05", "배송완료"),
        PARTIAL_DELIVERY("D03", "부분배송"),
        EXCHANGE_DELIVERY("O11", "교환배송"),
        PICKUP("D07", "방문수령완료"),
        NULL("", "");

        private final String code;
        private final String name;

        Status(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
                Stream.of(values()).collect(
                        Collectors.toMap(Status::getCode, Status::name)
                )
        );

        public static Status of(final String code) {
            if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
                return Status.NULL;

            return Status.valueOf(CODE_MAP.get(code));
        }
    }

}
