package kr.wrightbrothers.apps.common.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ChargeType {

    PAY("1", "유료"),
    FREE("2", "무료"),
    TERMS_FREE("3", "조건부 무료"),
    NULL("", "");

    private final String type;
    private final String name;

    ChargeType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(ChargeType::getType, ChargeType::name)
            )
    );

    public static ChargeType of(final String type) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(type)))
            return ChargeType.NULL;

        return ChargeType.valueOf(CODE_MAP.get(type));
    }

}
