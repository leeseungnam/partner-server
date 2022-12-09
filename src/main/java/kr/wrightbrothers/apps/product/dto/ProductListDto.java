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
        /** 파트너 코드 */
        private String partnerCode;

        /** 전시 상태 */
        private String[] displayFlag;

        /** 상품 상태 */
        private String[] status;

        /** 조회 종류 */
        private String rangeType;

        /** 시작 일자 */
        private String startDay;

        /** 종료 일자 */
        private String endDay;

        /** 키워드 종류 */
        private String keywordType;

        /** 키워드 값 */
        private String keywordValue;

        /** 정렬 타입 */
        private String sortType;

        /** 여러검색 조건 */
        private String[] keywordValueList;

        // 여러 상품 검색을 위해 구분자인 ; Split 처리
        public void splitKeywordValue() {
            if (ObjectUtils.isEmpty(this.keywordValue))
                return;

            this.keywordValueList = this.keywordValue.split(",");
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        /** 상품 코드 */
        private String productCode;

        /** 브랜드 */
        private String brandName;

        /** 대 카테고리 */
        private String categoryOneName;

        /** 중 카테고리 */
        private String categoryTwoName;

        /** 소 카테고리 */
        private String categoryThrName;

        /** 상품명 */
        private String productName;

        /** 상품 상태 코드 */
        private String productStatusCode;

        /** 상품 상태 명 */
        private String productStatusName;

        /** 전시 상태 */
        private String displayFlag;

        /** 전시 상태 명 */
        private String displayFlagName;

        /** 재고 수량 */
        private int productStockQty;

        /** 판매가 */
        private Long finalSellAmount;

        /** 옵션 여부 */
        private String productOptionFlag;

        /** 등록일시 */
        private String createDate;

        /** 수정일시 */
        private String updateDate;

        /** 등록자 아이디 */
        private String createUserId;

        /** 등록자 이름 */
        private String createUserName;

        // 상품 상태 ENUM 처리
        public void setProductStatusName(String productStatusName) {
            this.productStatusName = ProductStatusCode.of(productStatusName).getName();
        }

        public void setCreateUserName(String createUserName) {
            if (ObjectUtils.isEmpty(createUserName))
                createUserName = "라이트브라더스";
            this.createUserName = createUserName;
        }
    }

}
