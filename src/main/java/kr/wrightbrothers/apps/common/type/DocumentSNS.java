package kr.wrightbrothers.apps.common.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DocumentSNS {
    REQUEST_INSPECTION("파트너검수요청"),
    RESULT_INSPECTION("파트너검수결과"),
    UPDATE_PRODUCT("상품수정"),
    REQUEST_CANCEL_PAYMENT("파트너결제취소요청"),
    REQUEST_RETURN_PRODUCT("파트너반품완료요청")

    ;

    private final String name;

    DocumentSNS(String name) {
        this.name = name;
    }
}
