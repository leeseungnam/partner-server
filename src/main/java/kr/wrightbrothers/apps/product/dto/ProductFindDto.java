package kr.wrightbrothers.apps.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ProductFindDto {

    @Getter @Builder
    @AllArgsConstructor
    public static class Param {
        /** 스토어 코드 */
        private String partnerCode;

        /** 상품 코드 */
        private String productCode;
    }

    @Getter @Builder
    public static class ResBody {
        /** 상품 기본 정보 */
        private ProductDto.ResBody product;

        /** 기본 스펙 정보 */
        private BasicSpecDto.ResBody basicSpec;

        /** 판매 정보 */
        private SellInfoDto.ResBody sellInfo;

        /** 옵션 정보 */
        private List<OptionDto.ResBody> optionList;

        /** 배송 정보 */
        private DeliveryDto.ResBody delivery;

        /** 상품 정보 고시 */
        private InfoNoticeDto.ResBody infoNotice;

        /** 안내 사항 정보 */
        private GuideDto.ResBody guide;

        /** 반려 사유 */
        private String rejectReason;

        // 검수 반려 사유
        public void setRejectReason(String reason) {
            this.rejectReason = reason;
        }
    }

}
