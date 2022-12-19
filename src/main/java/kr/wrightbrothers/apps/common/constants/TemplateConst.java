package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateConst {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Type {
        DELIVERY("T01", "배송지"),
        AS_GUIDE("T02", "A/S안내"),
        DELIVERY_GUIDE("T03", "배송안내"),
        RETURN_GUIDE("T04", "반품안내"),
        QNA_GUIDE("T05", "자주묻는질문"),
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
