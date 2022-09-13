package kr.wrightbrothers.apps.product.dto;

import kr.wrightbrothers.apps.common.AbstractPageDto;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

public class ProductListDto {

    @Data
    @Jacksonized
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    public static class Param extends AbstractPageDto {
        private String partnerCode;             // 파트너 코드
        private String[] displayFlag;           // 전시 상태
        private String[] status;                // 상품 상태
        private String rangeType;               // 조회 종류
        private String startDay;                // 시작 일자
        private String endDay;                  // 종료 일자
        private String keywordType;             // 키워드 종류
        private String keywordValue;            // 키워드 값
        private String[] keywordValueList;      // 여러검색 조건

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
        private String productCode;             // 상품 코드
        private String brandName;               // 브랜드
        private String categoryOneName;         // 대 카테고리
        private String categoryTwoName;         // 중 카테고리
        private String categoryThrName;         // 소 카테고리
        private String productName;             // 상품명
        private String productStatus;           // 상품 상태
        private String displayFlag;             // 전시 상태
        private int productStockQty;            // 재고 수량
        private Long finalSellAmount;           // 판매가
        private String productOptionFlag;       // 옵션 여부
        private String createDate;              // 등록일시
        private String updateDate;              // 수정일시
        private String createUserId;            // 등록자 아이디
        private String createUserName;          // 등록자 이름

        // 상품 상태 ENUM 처리
        public void setProductStatus(String productStatus) {
            this.productStatus = ProductStatusCode.of(productStatus).getName();
        }
    }

}
