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
public enum OrderStatusCode {
    COMPLETE_ORDER("O05", "주문완료"),
    READY_PRODUCT("D01", "상품준비중"),
    CONFIRM_PURCHASE("C05", "구매확정"),
    CANCEL_ORDER("O08", "주문취소"),
    REQUEST_CANCEL("O06", "취소요청"),
    COMPLETE_CANCEL("O07", "취소완료"),
    START_DELIVERY("D02", "배송중"),
    PARTIAL_DELIVERY("D03", "부분배송"),
    FINISH_DELIVERY("D05", "배송완료"),
    FAIL_ORDER("O99", "주문실패"),
    REQUEST_RETURN("R01", "반품요청"),
    WITHDRAWAL_RETURN("R02", "반품철회"),
    START_RETURN("R03", "반품진행"),
    NON_RETURN("R04", "반품불가"),
    COMPLETE_RETURN("R05", "반품완료"),
    EXCHANGE_DELIVERY("O11", "교환배송"),
    COMPLETE_PICKUP("O12", "픽업완료"),
    NULL("", "");

    private final String code;
    private final String name;

    OrderStatusCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(OrderStatusCode::getCode, OrderStatusCode::name)
            )
    );

    public static OrderStatusCode of(final String code) {
        if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
            return OrderStatusCode.NULL;

        return OrderStatusCode.valueOf(CODE_MAP.get(code));
    }
}
