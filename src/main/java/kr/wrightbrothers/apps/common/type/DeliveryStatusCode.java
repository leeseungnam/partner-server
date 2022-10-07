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
public enum DeliveryStatusCode {
    READY_PRODUCT("D01", "상품준비중"),
    START_DELIVERY("D02", "배송중"),
    FINISH_DELIVERY("D05", "배송완료"),
    PARTIAL_DELIVERY("D03", "부분배송"),
    EXCHANGE_DELIVERY("O11", "교환배송"),
    NULL("", "");

    private final String code;
    private final String name;

    DeliveryStatusCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(DeliveryStatusCode::getCode, DeliveryStatusCode::name)
            )
    );

    public static DeliveryStatusCode of(final String code) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
            return DeliveryStatusCode.NULL;

        return DeliveryStatusCode.valueOf(CODE_MAP.get(code));
    }
}
