package kr.wrightbrothers.apps.product.dto;

import kr.wrightbrothers.apps.file.dto.FileUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInsertDto {
    private ProductDto.ReqBody product;         // 상품 기본 정보
    private BasicSpecDto.ReqBody basicSpec;     // 기본 스펙 정보
    private SellInfoDto.ReqBody sellInfo;       // 판매 정보
    private List<OptionDto.ReqBody> optionList; // 옵션 정보
    private DeliveryDto.ReqBody delivery;       // 배송 정보
    private InfoNoticeDto.ReqBody infoNotice;   // 상품 정보 고시
    private GuideDto.ReqBody guide;             // 안내사항 정보
    private List<FileUpdateDto> fileList;       // 상품 등록 이미지

    public void setUserId(String userId) {
        // 필수 데이터 입력 부분
        product.setUserId(userId);
        sellInfo.setUserId(userId);
        delivery.setUserId(userId);
        infoNotice.setUserId(userId);
        guide.setUserId(userId);
        optionList.forEach(option -> option.setUserId(userId));
        fileList.forEach(file -> file.setUserId(userId));
        if (!ObjectUtils.isEmpty(basicSpec))
            basicSpec.setUserId(userId);
    }

    public void setProductCode(String productCode) {
        // 필수 데이터 입력 부분
        product.setProductCode(productCode);
        sellInfo.setProductCode(productCode);
        delivery.setProductCode(productCode);
        infoNotice.setProductCode(productCode);
        guide.setProductCode(productCode);
        optionList.forEach(option -> option.setProductCode(productCode));

        if (!ObjectUtils.isEmpty(productCode))
            basicSpec.setProductCode(productCode);
    }
}
