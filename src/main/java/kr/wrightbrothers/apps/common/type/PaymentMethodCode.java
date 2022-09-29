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
public enum PaymentMethodCode {
    CARD("P01", "신용카드"),
    BANK("P02", "계좌이체"),
    NON_BANK("P03", "무통장"),
    INI_RENTAL("P04", "이니렌탈"),
    SALES("P05", "판매대금"),
    PAYCO("P06", "페이코"),
    KAKAO_PAY("P07", "카카오페이"),
    SAMSUNG_PAY("P08", "삼성페이"),
    LOTTE_PAY("P09", "L.Pay"),
    NAVER_PAY("P10", "네이버페이"),
    LOTTE_POINT("P11", "L.Point"),
    WB_PAY("P12", "라브페이"),
    CASH("P13", "현금"),
    SSP("P15", "S.S.P"),
    ETC("P14", "기타"),
    NULL("", "");

    private final String code;
    private final String name;

    PaymentMethodCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(PaymentMethodCode::getCode, PaymentMethodCode::name)
            )
    );

    public static PaymentMethodCode of(final String code) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
            return PaymentMethodCode.NULL;

        return PaymentMethodCode.valueOf(CODE_MAP.get(code));
    }
}
