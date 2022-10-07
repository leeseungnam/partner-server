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
public enum DeliveryType {

    PARCEL("D01", "택배"),
    FREIGHT("D07", "화물"),
    NULL("", "");

    private final String type;
    private final String name;

    DeliveryType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(DeliveryType::getType, DeliveryType::name)
            )
    );

    public static DeliveryType of(final String type) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(type)))
            return DeliveryType.NULL;

        return DeliveryType.valueOf(CODE_MAP.get(type));
    }
}
