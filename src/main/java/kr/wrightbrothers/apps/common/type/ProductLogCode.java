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
public enum ProductLogCode {
    REGISTER("L01", "등록"),
    INSPECTION("L02", "검수"),
    MODIFY("L03", "수정"),
    REJECT("L04", "반려"),
    NULL("", "");

    private final String code;
    private final String name;

    ProductLogCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(ProductLogCode::getCode, ProductLogCode::name)
            )
    );

    public static ProductLogCode of(final String code) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
            return ProductLogCode.NULL;

        return ProductLogCode.valueOf(CODE_MAP.get(code));
    }
}
