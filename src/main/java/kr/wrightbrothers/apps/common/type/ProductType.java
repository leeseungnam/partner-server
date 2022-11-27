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
public enum ProductType {
    NEW("P05", "신품"),
    RECYCLING("P04", "재생"),
    NULL("", "");

    private final String type;
    private final String name;

    ProductType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(ProductType::getType, ProductType::name)
            )
    );

    public static ProductType of(final String type) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(type)))
            return ProductType.NULL;

        return ProductType.valueOf(CODE_MAP.get(type));
    }
}
