package kr.wrightbrothers.apps.product;

import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.constants.ProductConst;
import kr.wrightbrothers.apps.common.util.ProductUtil;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ProductController extends WBController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final ProductService productService;
    private final ProductUtil productUtil;

    @GetMapping("/products")
    public WBModel findProductList(@RequestParam(required = false) String[] displayFlag,
                                   @RequestParam(required = false) String[] status,
                                   @RequestParam(required = false) String rangeType,
                                   @RequestParam(required = false) String startDay,
                                   @RequestParam(required = false) String endDay,
                                   @RequestParam String keywordType,
                                   @RequestParam(required = false) String keywordValue,
                                   @RequestParam String sortType,
                                   @RequestParam int count,
                                   @RequestParam int page,
                                   @AuthenticationPrincipal UserPrincipal user
    ) {
        WBModel res = new WBModel();
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
        res.addObject(WBKey.WBModel.DefaultDataKey, productService.findProductList(paramDto, true));
        res.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return res;
    }

    @GetMapping("/products/excel")
    public void productExcelDownload(@RequestParam(required = false) String[] displayFlag,
                                     @RequestParam(required = false) String[] status,
                                     @RequestParam(required = false) String rangeType,
                                     @RequestParam(required = false) String startDay,
                                     @RequestParam(required = false) String endDay,
                                     @RequestParam String keywordType,
                                     @RequestParam(required = false) String keywordValue,
                                     @RequestParam String sortType,
                                     @RequestParam int count,
                                     @RequestParam int page,
                                     @AuthenticationPrincipal UserPrincipal user,
                                     HttpServletResponse response
    ) throws IOException {
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
        List<ProductListDto.Response> productList = productService.findProductList(paramDto, false);

        // 엑셀 다운로드
        productService.makeExcelFile(
                productList.stream()
                        .map(ProductListDto.Response::getProductCode)
                        .collect(Collectors.toList()),
                response
        );
    }

    @UserPrincipalScope
    @PostMapping("/products")
    public WBModel insertProduct(@Valid @RequestBody ProductInsertDto paramDto,
                                 @AuthenticationPrincipal UserPrincipal user) {
        // 상품타입 설정
        paramDto.setProductType(user.getUserAuth().getPartnerKind());
        // 상품코드 생성
        paramDto.setProductCode(
                productUtil.generateProductCode(paramDto.getProduct().getCategoryTwoCode())
        );
        // 검수대기 상태 변경
        paramDto.getSellInfo().setProductStatusCode(ProductConst.Status.PRODUCT_INSPECTION.getCode());
        paramDto.getSellInfo().setDisplayFlag("N");
        // 추가 유효성 검사
        paramDto.validProduct();

        // 상품정보 등록
        productService.insertProduct(paramDto);
        log.info("Product Registration Complete. Product Code::{}", paramDto.getProduct().getProductCode());

        return insertMsgResponse(messageSourceAccessor);
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

    @UserPrincipalScope
    @PutMapping("/products")
    public WBModel updateProduct(@Valid @RequestBody ProductUpdateDto paramDto,
                                 @AuthenticationPrincipal UserPrincipal user) {
        // 상품타입 설정
        paramDto.setProductType(user.getUserAuth().getPartnerKind());
        paramDto.setProductCode(paramDto.getProductCode());
        // 추가 유효성 검사
        paramDto.validProduct();
        // 상품정보 수정
        productService.updateProduct(paramDto);
        log.info("Product Edit Complete. Product Code::{}", paramDto.getProductCode());

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @PatchMapping("/products")
    public WBModel updateProductStatus(@Valid @RequestBody StatusUpdateDto paramDto) {
        // 상품 일괄 상태 변경
        productService.updateProductStatus(paramDto);
        log.info("Product Status Edit Complete. Status Type::{}, Status Value::{}", paramDto.getStatusType(), paramDto.getStatusValue());

        return noneMgsResponse(messageSourceAccessor);
    }

    @DeleteMapping("/products")
    public WBModel deleteProduct(@RequestParam String[] productCodeList,
                                 @AuthenticationPrincipal UserPrincipal user) {
        // 검수대기 상품 삭제
        productService.deleteProduct(new ProductDeleteDto(user.getUserAuth().getPartnerCode(), productCodeList));

        return noneMgsResponse(messageSourceAccessor);
    }

}
