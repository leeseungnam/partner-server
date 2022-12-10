package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaymentConst {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Method {
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

        Method(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
                Stream.of(values()).collect(
                        Collectors.toMap(Method::getCode, Method::name)
                )
        );

        public static Method of(final String code) {
            if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
                return Method.NULL;

            return Method.valueOf(CODE_MAP.get(code));
        }
    }

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Status {
        WAIT_DEPOSIT("S01", "입금대기"),
        CANCEL_PAYMENT("S08", "결제취소"),
        COMPLETE_PAYMENT("S10", "결제완료"),
        PARTIAL_CANCEL_PAYMENT("S02", "부분최소"),
        WITHDRAWAL_CONTRACT("S03", "계약철회"),
        COMPLETE_CONTRACT("S04", "계약완료"),
        REQUEST_CANCEL_PAYMENT("S06", "결제취소 요청"),
        FAIL_CANCEL_PAYMENT("S09", "결제취소 실패"),
        NULL("", "");

        private final String code;
        private final String name;

        Status(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
                Stream.of(values()).collect(
                        Collectors.toMap(Status::getCode, Status::name)
                )
        );

        public static Status of(final String code) {
            if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
                return Status.NULL;

            return Status.valueOf(CODE_MAP.get(code));
        }
    }

}
