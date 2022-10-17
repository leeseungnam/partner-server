package kr.wrightbrothers.apps.common.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DocumentSNS {
    REQ_INSPECTION("파트너검수요청"),
    RST_INSPECTION("파트너심사결과"),
    UPDATE_PRODUCT("상품수정");

    private final String name;

    DocumentSNS(String name) {
        this.name = name;
    }
}
