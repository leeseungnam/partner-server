package kr.wrightbrothers.apps.product;

import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.apps.sign.dto.UserDetailDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
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
                                   @RequestParam String sortType,
                                   @RequestParam int count,
                                   @RequestParam int page,
                                   @AuthenticationPrincipal UserPrincipal user
    ) {
        WBModel response = new WBModel();
        ProductListDto.Param paramDto = ProductListDto.Param.builder()
                .partnerCode(user.getUserAuth().getPartnerCode())
                .displayFlag(displayFlag)
                .status(status)
                .rangeType(rangeType)
                .startDay(startDay)
                .endDay(endDay)
                .keywordType(keywordType)
                .keywordValue(keywordValue)
                .sortType(sortType)
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
    public WBModel insertProduct(@RequestBody ProductInsertDto paramDto,
                                 @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.getProduct().setPartnerCode(user.getUserAuth().getPartnerCode());
        paramDto.setProductCode(
                productService.generateProductCode(paramDto.getProduct().getCategoryTwoCode())
        );

        // 상품정보 등록
        productService.insertProduct(paramDto);

        return noneDataResponse();
    }

    @GetMapping("/products/{productCode}")
    public WBModel findProduct(@PathVariable String productCode,
                               @AuthenticationPrincipal UserPrincipal user) {
        // 상품 상세 정보
        return defaultResponse(productService.findProduct(
                ProductFindDto.Param.builder()
                        // Security Custom UserDetail 객체를 통해 파트너 코드 추출
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .productCode(productCode)
                .build()
        ));
    }

    @PutMapping("/products")
    public WBModel updateProduct(@RequestBody ProductUpdateDto paramDto,
                                 @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.getProduct().setPartnerCode(user.getUserAuth().getPartnerCode());
        paramDto.setProductCode(paramDto.getProductCode());

        // 상품정보 수정
        productService.updateProduct(paramDto);

        return noneDataResponse();
    }

    @PatchMapping("/products")
    public WBModel updateProductStatus(@RequestBody StatusUpdateDto paramDto,
                                       @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.setPartnerCode(user.getUserAuth().getPartnerCode());

        // 상품 일괄 상태 변경
        productService.updateProductStatus(paramDto);

        return noneDataResponse();
    }

}
