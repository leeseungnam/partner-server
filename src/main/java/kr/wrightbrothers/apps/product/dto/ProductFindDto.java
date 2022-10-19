package kr.wrightbrothers.apps.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ProductFindDto {

    @Getter
    @Builder
    public static class Param {
        private String partnerCode;                 // 스토어 코드
        private String productCode;                 // 상품 코드
    }

    @Getter
    @Builder
    public static class ResBody {
        private ProductDto.ResBody product;         // 상품 기본 정보
        private BasicSpecDto.ResBody basicSpec;     // 기본 스펙 정보
        private SellInfoDto.ResBody sellInfo;       // 판매 정보
        private List<OptionDto.ResBody> optionList; // 옵션 정보
        private DeliveryDto.ResBody delivery;       // 배송 정보
        private InfoNoticeDto.ResBody infoNotice;   // 상품 정보 고시
        private GuideDto.ResBody guide;             // 안내 사항 정보
        private String rejectReason;                // 반려 사유

        // 검수 반려 사유
        public void setRejectReason(String reason) {
            this.rejectReason = reason;
        }
    }

}
