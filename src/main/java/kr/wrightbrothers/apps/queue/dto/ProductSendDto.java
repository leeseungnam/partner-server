package kr.wrightbrothers.apps.queue.dto;

import kr.wrightbrothers.apps.product.dto.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductSendDto {
    private String partnerCode;                 // 파트너 코드
    private ProductDto.ResBody product;         // 상품 기본 정보
    private BasicSpecDto.ResBody basicSpec;     // 기본 스펙 정보
    private SellInfoDto.ResBody sellInfo;       // 판매 정보
    private List<OptionDto.Queue> optionList; // 옵션 정보
    private DeliveryDto.ResBody delivery;       // 배송 정보
    private InfoNoticeDto.ResBody infoNotice;   // 상품 정보 고시
    private GuideDto.ResBody guide;             // 안내 사항 정보
}
