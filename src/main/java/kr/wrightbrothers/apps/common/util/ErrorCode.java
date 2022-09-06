package kr.wrightbrothers.apps.common.util;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_CHANGE_CPLB_STATUS(4),
    DUPLICATED_CPLB_CANCEL(5),
    NOT_ALLOW_CPLB_CANCEL_AFTER_INCOME(6),
    NOT_ALLOW_CPLB_CANCEL_BEFORE_INCOME(18),
    INVALID_STATUS(7),
    DUPLICATED_PARTS(11),
    DUPLICATED_BRAND_KO(12),
    DUPLICATED_BRAND_EN(12),
    DUPLICATED_BRAND_MODEL_KO(12),
    DUPLICATED_BRAND_MODEL_EN(12),
    NOT_FOUND_EXCEL_FILE(20),
    INVALID_EXCEL_FILE(21),
    INVALID_PARAM(9999);

    final int errCode;

    ErrorCode(int errCode) {
        this.errCode = errCode;
    }
}
