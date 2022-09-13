package kr.wrightbrothers.apps.product;

import kr.wrightbrothers.apps.product.dto.ProductListDto;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    public WBModel insertProduct() {

        return noneDataResponse();
    }

}
