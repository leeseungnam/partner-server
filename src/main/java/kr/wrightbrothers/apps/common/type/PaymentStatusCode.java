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
public enum PaymentStatusCode {
    WAIT_DEPOSIT("S01", "입금대기"),
    CANCEL_PAYMENT("S08", "결제취소"),
    COMPLETE_PAYMENT("S10", "결제완료"),
    PARTIAL_CANCEL_PAYMENT("S02", "부분최소"),
    WITHDRAWAL_CONTRACT("S03", "계약철회"),
    COMPLETE_CONTRACT("S04", "계약완료"),
    REQUEST_CANCEL_PAYMENT("S09", "결제취소 요청"),
    NULL("", "");

    private final String code;
    private final String name;

    PaymentStatusCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(PaymentStatusCode::getCode, PaymentStatusCode::name)
            )
    );

    public static PaymentStatusCode of(final String code) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
            return PaymentStatusCode.NULL;

        return PaymentStatusCode.valueOf(CODE_MAP.get(code));
    }
}
