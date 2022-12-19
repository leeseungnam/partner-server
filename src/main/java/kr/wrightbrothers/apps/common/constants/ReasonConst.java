package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReasonConst {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum NonReturn {

        USE("R01", "상품 조립 및 사용"),
        OMISSION("R02", "부속품 누락"),
        DAMAGE("R03", "고객의 사유로 상품의 훼손"),
        NULL("", "");

        private final String code;
        private final String name;

        NonReturn(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
                Stream.of(values()).collect(
                        Collectors.toMap(NonReturn::getCode, NonReturn::name)
                )
        );

        public static NonReturn of(final String type) {
            if (ObjectUtils.isEmpty(CODE_MAP.get(type)))
                return NonReturn.NULL;

            return NonReturn.valueOf(CODE_MAP.get(type));
        }

    }

}
