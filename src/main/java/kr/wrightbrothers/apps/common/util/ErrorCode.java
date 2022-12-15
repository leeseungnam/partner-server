package kr.wrightbrothers.apps.common.util;

import lombok.Getter;

@Getter
public enum ErrorCode {

    NO_CONTENT(2401),
    FORBIDDEN(4300),
    FORBIDDEN_REFRESH(4301),
    FORBIDDEN_LOGIN(4302),
    FORBIDDEN_LOGOUT(4303),
    UNAUTHORIZED(4100),
    UNAUTHORIZED_LOGIN(4101),
    UNAUTHORIZED_TOKEN(4102),
    INVALID_PRODUCT_STATUS(4000),
    INVALID_PRODUCT_DISPLAY(4000),
    INVALID_PARAM(4001),
    INVALID_TEXT_SIZE(4002),
    INVALID_MONEY_MIN(4003),
    INVALID_MONEY_MAX(4004),
    INVALID_NUMBER_MIN(4005),
    INVALID_NUMBER_MAX(4006),
    INVALID_BOOLEAN(4007),
    UNABLE_CANCEL_PAYMENT(4008),
    UNABLE_CANCEL_PARTIAL_PAYMENT(4009),
    ALREADY_CANCELED_PAYMENT(4010),
    ALREADY_RETURN(4010),
    INVALID_DELIVERY_PREPARING(4000),
    DUPLICATION_OBJECT(4011),
    COMPLETE_DELIVERY(4012),
    END_OF_SALE(4013),
    INVALID_PRODUCT_STOCK(4014),
    INVALID_DELIVERY_TYPE(4015),
    INVALID_IMAGE_MAX(4016),
    INVALID_RANGE(4017),
    INVALID_PARTNER_NAME(4018),
    INVALID_PARTNER_BISNO(4019),
    INTERNAL_SERVER(5000),
    ETC(9999)
    ;

    final int errCode;

    ErrorCode(int errCode) {
        this.errCode = errCode;
    }
}
