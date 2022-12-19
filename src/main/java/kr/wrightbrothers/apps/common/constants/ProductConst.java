package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductConst {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Category {
        BIKE("B0001", "자전거"),
        CLOTH("C0001", "의류"),
        ACCESSORY("D0001", "용품"),
        PARTS("E0001", "부품"),
        ETC("F0001", "기타"),
        NULL("", "");

        private final String code;
        private final String name;

        Category(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
                Stream.of(values()).collect(
                        Collectors.toMap(Category::getCode, Category::name)
                )
        );

        public static Category of(final String code) {
            if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
                return Category.NULL;

            return Category.valueOf(CODE_MAP.get(code));
        }
    }

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Type {
        NEW("P05", "신품"),
        RECYCLING("P04", "재생"),
        NULL("", "");

        private final String type;
        private final String name;

        Type(String type, String name) {
            this.type = type;
            this.name = name;
        }

        private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
                Stream.of(values()).collect(
                        Collectors.toMap(Type::getType, Type::name)
                )
        );

        public static Type of(final String type) {
            if (ObjectUtils.isEmpty(CODE_MAP.get(type)))
                return Type.NULL;

            return Type.valueOf(CODE_MAP.get(type));
        }
    }

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Log {
        REGISTER("L01", "등록"),
        INSPECTION("L02", "검수"),
        MODIFY("L03", "수정"),
        REJECT("L04", "반려"),
        NULL("", "");

        private final String code;
        private final String name;

        Log(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
                Stream.of(values()).collect(
                        Collectors.toMap(Log::getCode, Log::name)
                )
        );

        public static Log of(final String code) {
            if (ObjectUtils.isEmpty(CODE_MAP.get(code)))
                return Log.NULL;

            return Log.valueOf(CODE_MAP.get(code));
        }
    }

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Status {
        PRODUCT_INSPECTION("S06", "검수대기"),
        SALE("S01", "판매중"),
        RESERVATION("S02", "예약중"),
        SOLD_OUT("S03", "판매완료"),
        END_OF_SALE("S08", "판매종료"),
        REJECT_INSPECTION("S10", "검수반려"),
        APPROVAL_INSPECTION("S09", "검수승인"),
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
