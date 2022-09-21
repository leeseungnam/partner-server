package kr.wrightbrothers.apps.common.util;

import lombok.Getter;

@Getter
public enum ErrorCode {

    FORBIDDEN(4030),
    UNAUTHORIZED(4010),
    VALID_PRODUCT_STATUS(4000),
    VALID_PRODUCT_DISPLAY(4000)
    ;

    final int errCode;

    ErrorCode(int errCode) {
        this.errCode = errCode;
    }
}
