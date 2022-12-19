package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StorageConst {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Type {
        PRODUCT("S01", "상품"),
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

}
