package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Partner {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Status {
        STOP("1", "운영중"),
        RUN("2", "운영중")
        ;

        private final String code;
        private final String name;

        Status(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, Partner.Status> CODE_MAP =
                Stream.of(values()).collect(Collectors.toMap(Partner.Status::getCode, Function.identity()));

        public static Partner.Status valueOfCode(String code) {
            return CODE_MAP.get(code);
        }
    }
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Classification {
        GENERAL_TAXPATER("1", "일반과세자"),
        SIMPLIFIED_TAXPAYER("2", "간이과세자"),
        UNIT_TAXPATER("3", "단위과세자"),
        CORPORATE_BUSINESS_OPERATOR("4", "법인사업자"),
        DUTYFREE_BUSINESS_OPERATOR("5", "면세사업자"),
        OTHER_BUSINESS_OPERATOR("6", "기타사업자")
        ;

        private final String code;
        private final String name;

        Classification(String code, String name) {
            this.code = code;
            this.name = name;
        }

        private static final Map<String, Partner.Classification> CODE_MAP =
                Stream.of(values()).collect(Collectors.toMap(Partner.Classification::getCode, Function.identity()));

        public static Partner.Classification valueOfCode(String code) {
            return CODE_MAP.get(code);
        }
    }
    public static class Contract {

        @Getter
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        public enum Status {
            REQUEST("C01", "심사중"),
            //  최소 계약 or 재계약(중지 -> 운영)
            COMPLETE("C02", "심사승인"),
            REJECT("C03", "심사반려"),
            //  자동 갱신
            AUTOMATIC("C04", "계약갱신"),
            WITHDRAWAL("C05", "계약종료"),
            CANCEL("C06", "계약철회"),
            VIOLATE("C07", "계약/정책 위한")
            ;

            private final String code;
            private final String name;

            Status(String code, String name) {
                this.code = code;
                this.name = name;
            }

            private static final Map<String, Partner.Contract.Status> CODE_MAP =
                    Stream.of(values()).collect(Collectors.toMap(Partner.Contract.Status::getCode, Function.identity()));

            public static Partner.Contract.Status valueOfCode(String code) {
                return CODE_MAP.get(code);
            }
        }

        @Getter
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        public enum Bank {
            산업은행("02", "산업은행"),
            기업은행("03", "기업은행"),
            KB국민은행("04", "KB국민은행"),
            수협은행("07", "수협은행"),
            NH농협은행("11", "NH농협은행"),
            우리은행("20", "우리은행"),
            SC제일은행("23", "SC제일은행"),
            씨티은행("27", "씨티은행"),
            DGB대구은행("31", "DGB대구은행"),
            BNK부산은행("32", "BNK부산은행"),
            광주은행("34", "광주은행"),
            제주은행("35", "제주은행"),
            전북은행("37", "전북은행"),
            BNK경남은행("39", "BNK경남은행"),
            새마을금고("45", "새마을금고"),
            신협중앙회("48", "신협중앙회"),
            상호저축은행("50", "상호저축은행"),
            우체국("71", "우체국"),
            KEB하나은행("81", "KEB하나은행"),
            신한은행("88", "신한은행"),
            케이뱅크("89", "케이뱅크"),
            카카오뱅크("90", "카카오뱅크"),
            토스뱅크("92", "토스뱅크")
            ;

            private final String code;
            private final String name;

            Bank(String code, String name) {
                this.code = code;
                this.name = name;
            }

            private static final Map<String, Partner.Contract.Bank> CODE_MAP =
                    Stream.of(values()).collect(Collectors.toMap(Partner.Contract.Bank::getCode, Function.identity()));

            public static Partner.Contract.Bank valueOfCode(String code) {
                return CODE_MAP.get(code);
            }
        }
    }
}
