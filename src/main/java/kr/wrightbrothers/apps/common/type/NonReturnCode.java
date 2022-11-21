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
public enum NonReturnCode {

    USE("R01", "상품 조립 및 사용"),
    OMISSION("R02", "부속품 누락"),
    DAMAGE("R03", "고객의 사유로 상품의 훼손"),
    NULL("", "");

    private final String code;
    private final String name;

    NonReturnCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(NonReturnCode::getCode, NonReturnCode::name)
            )
    );

    public static NonReturnCode of(final String type) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(type)))
            return NonReturnCode.NULL;

        return NonReturnCode.valueOf(CODE_MAP.get(type));
    }

}
