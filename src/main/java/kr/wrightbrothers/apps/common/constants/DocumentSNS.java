package kr.wrightbrothers.apps.common.constants;

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
    NOTI_KAKAO_SINGLE("알림톡전송"),
    NOTI_SMS_SINGLE("SMS전송"),
    REQUEST_INSPECTION("파트너검수요청"),
    RESULT_INSPECTION("파트너검수결과"),
    REQUEST_INSPECTION_PARTNER("입점몰심사요청"),
    RESULT_INSPECTION_PARTNER("입점몰심사결과"),
    UPDATE_PRODUCT("파트너상품수정"),
    UPDATE_PARTNER("파트너변경"),
    UPDATE_HISTORY("P-HISTORY"),
    UPDATE_ORDER("P-ORDER"),
    UPDATE_ORDER_STATUS("P-STATUS-CHANGE"),
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
