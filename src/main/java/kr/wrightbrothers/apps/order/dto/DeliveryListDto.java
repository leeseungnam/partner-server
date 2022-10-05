package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.AbstractPageDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

public class DeliveryListDto {

    @Getter
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class Param extends AbstractPageDto {
        private String partnerCode;         // 파트너 코드
        private String[] deliveryType;      // 배송 방법
        private String[] deliveryStatus;    // 배송 상태
        private String rangeType;           // 조회 종류
        private String startDay;            // 시작 일자
        private String endDay;              // 종료 일자
        private String keywordType;         // 키워드 종류
        private String keywordValue;        // 키워드 값
        private String[] keywordValueList;  // 여러검색 조건

        // 여러 상품 검색을 위해 구분자인 ; Split 처리
        public void splitKeywordValue() {
            if (ObjectUtils.isEmpty(this.keywordValue))
                return;

            this.keywordValueList = this.keywordValue.split(";");
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {

    }

}
