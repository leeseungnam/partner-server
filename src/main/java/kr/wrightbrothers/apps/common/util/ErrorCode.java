package kr.wrightbrothers.apps.common.util;

import lombok.Getter;

@Getter
public enum ErrorCode {

    FORBIDDEN(403),
    UNAUTHORIZED(401),
    VALID_PRODUCT_STATUS(4001)
    ;

    final int errCode;

    ErrorCode(int errCode) {
        this.errCode = errCode;
    }
}
