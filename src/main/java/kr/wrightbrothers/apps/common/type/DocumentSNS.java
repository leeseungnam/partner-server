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
public enum DocumentSNS {
    REQUEST_INSPECTION("파트너검수요청"),
    RESULT_INSPECTION("파트너검수결과"),
    REQUEST_INSPECTION_PARTNER("입점몰심사요청"),
    RESULT_INSPECTION_PARTNER("입점몰심사결과"),
    UPDATE_PRODUCT("상품수정"),
    UPDATE_PARTNER("입점몰변경"),
    REQUEST_CANCEL_PAYMENT("P-CANCEL"),
    REQUEST_RETURN_PRODUCT("P-RETURN"),
    NULL("");
    ;

    private final String name;

    DocumentSNS(String name) {
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(DocumentSNS::getName, DocumentSNS::name)
            )
    );

    public static DocumentSNS of(final String name) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(name)))
            return DocumentSNS.NULL;

        return DocumentSNS.valueOf(CODE_MAP.get(name));
    }
}
