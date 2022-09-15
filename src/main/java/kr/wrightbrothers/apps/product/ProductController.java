package kr.wrightbrothers.apps.product;

import kr.wrightbrothers.apps.product.dto.ProductFindDto;
import kr.wrightbrothers.apps.product.dto.ProductInsertDto;
import kr.wrightbrothers.apps.product.dto.ProductListDto;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController extends WBController {

    private final ProductService productService;

    @GetMapping("/products")
    public WBModel findProductList(@RequestParam String[] displayFlag,
                                   @RequestParam String[] status,
                                   @RequestParam String rangeType,
                                   @RequestParam String startDay,
                                   @RequestParam String endDay,
                                   @RequestParam String keywordType,
                                   @RequestParam(required = false) String keywordValue,
                                   @RequestParam int count,
                                   @RequestParam int page
    ) {
        WBModel response = new WBModel();
        ProductListDto.Param paramDto = ProductListDto.Param.builder()
                .partnerCode("PT0000001")
                .displayFlag(displayFlag)
                .status(status)
                .rangeType(rangeType)
                .startDay(startDay)
                .endDay(endDay)
                .keywordType(keywordType)
                .keywordValue(keywordValue)
                .count(count)
                .page(page)
                .build();
        // 다건 검색조회 split 처리
        paramDto.splitKeywordValue();

        // 상품 목록 조회
        response.addObject(WBKey.WBModel.DefaultDataKey, productService.findProductList(paramDto));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

    @PostMapping("/products")
    public WBModel insertProduct(@RequestBody ProductInsertDto paramDto) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId("test@wrightbrothers.kr");
        paramDto.getProduct().setPartnerCode("PT0000001");
        paramDto.setProductCode(
                productService.generateProductCode(paramDto.getProduct().getCategoryTwoCode())
        );

        // 상품정보 등록
        productService.insertProduct(paramDto);

        return noneDataResponse();
    }

    @GetMapping("/products/{productCode}")
    public WBModel findProduct(@PathVariable String productCode) {
        // 상품 상세 정보
        return defaultResponse(productService.findProduct(
                ProductFindDto.Param.builder()
                        // Security Custom UserDetail 객체를 통해 파트너 코드 추출
                        .partnerCode("PT0000001")
                        .productCode(productCode)
                .build()
        ));
    }

}
