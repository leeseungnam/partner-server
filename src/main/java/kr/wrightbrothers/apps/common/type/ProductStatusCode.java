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
public enum ProductStatusCode {
    PRODUCT_INSPECTION("S06", "검수대기"),
    SALE("S01", "판매중"),
    RESERVATION("S02", "예약중"),
    SOLD_OUT("S03", "판매완료"),
    END_OF_SALE("S08", "판매종료"),
    REJECT_INSPECTION("S10", "검수반려"),
    APPROVAL_INSPECTION("S09", "검수승인"),
    NULL("", "");

    private final String code;
    private final String name;

    ProductStatusCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(ProductStatusCode::getCode, ProductStatusCode::name)
            )
    );

    public static ProductStatusCode of(final String code) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
            return ProductStatusCode.NULL;

        return ProductStatusCode.valueOf(CODE_MAP.get(code));
    }
}
