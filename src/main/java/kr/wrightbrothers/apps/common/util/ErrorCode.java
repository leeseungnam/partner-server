package kr.wrightbrothers.apps.common.util;

import lombok.Getter;

@Getter
public enum ErrorCode {

    FORBIDDEN(4030),
    UNAUTHORIZED(4010),
    INVALID_PRODUCT_STATUS(4000),
    INVALID_PRODUCT_DISPLAY(4000),
    INVALID_PARAM(4001),
    INVALID_TEXT_SIZE(4002),
    INVALID_MONEY_MIN(4003),
    INVALID_MONEY_MAX(4004),
    INVALID_NUMBER_MIN(4005),
    INVALID_NUMBER_MAX(4006),
    INVALID_BOOLEAN(4007),
    INTERNAL_SERVER(5000)
    ;

    final int errCode;

    ErrorCode(int errCode) {
        this.errCode = errCode;
    }
}
