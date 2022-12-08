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
public enum StorageType {
    PRODUCT("S01", "상품"),
    NULL("", "");

    private final String type;
    private final String name;

    StorageType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(StorageType::getType, StorageType::name)
            )
    );

    public static StorageType of(final String type) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(type)))
            return StorageType.NULL;

        return StorageType.valueOf(CODE_MAP.get(type));
    }
}
