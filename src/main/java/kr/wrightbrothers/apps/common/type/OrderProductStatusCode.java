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
public enum OrderProductStatusCode {
    COMPLETE_ORDER("O05", "주문완료"),
    READY_PRODUCT("D01", "상품준비중"),
    START_DELIVERY("D02", "배송중"),
    FINISH_DELIVERY("D05", "배송완료"),
    REQUEST_CANCEL("O06", "취소요청"),
    COMPLETE_CANCEL("O07", "취소완료"),
    REQUEST_RETURN("R01", "반품요청"),
    WITHDRAWAL_RETURN("R02", "반품취소"),
    START_RETURN("R03", "반품진행"),
    NON_RETURN("R04", "반품불가"),
    REQUEST_COMPLETE_RETURN("R06", "반품완료요청"),
    COMPLETE_RETURN("R05", "반품완료"),
    CONFIRM_PURCHASE("C05", "구매확정"),
    NULL("", "");

    private final String code;
    private final String name;

    OrderProductStatusCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(OrderProductStatusCode::getCode, OrderProductStatusCode::name)
            )
    );

    public static OrderProductStatusCode of(final String code) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
            return OrderProductStatusCode.NULL;

        return OrderProductStatusCode.valueOf(CODE_MAP.get(code));
    }
}
