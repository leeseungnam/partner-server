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
public enum CategoryCode {
    BIKE("B0001", "자전거"),
    CLOTH("C0001", "의류"),
    ACCESSORY("D0001", "용품"),
    PARTS("E0001", "부품"),
    ETC("F0001", "기타"),
    NULL("", "");

    private final String code;
    private final String name;

    CategoryCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(CategoryCode::getCode, CategoryCode::name)
            )
    );

    public static CategoryCode of(final String code) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
            return CategoryCode.NULL;

        return CategoryCode.valueOf(CODE_MAP.get(code));
    }
}
