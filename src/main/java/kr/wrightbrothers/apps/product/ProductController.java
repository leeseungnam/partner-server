package kr.wrightbrothers.apps.product;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.ProductUtil;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.apps.product.service.ProductService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = {"상품"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ProductController extends WBController {

    private final ProductService productService;
    private final ProductUtil productUtil;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "상품 목록 조회", notes = "등록된 상품의 목록 조회")
    @GetMapping("/products")
    public WBModel findProductList(@ApiParam(value = "전시여부") @RequestParam String[] displayFlag,
                                   @ApiParam(value = "판매상태") @RequestParam String[] status,
                                   @ApiParam(value = "조회기간 구분") @RequestParam String rangeType,
                                   @ApiParam(value = "조회기간 시작일") @RequestParam String startDay,
                                   @ApiParam(value = "조회기간 종료일") @RequestParam String endDay,
                                   @ApiParam(value = "키워드 구분") @RequestParam String keywordType,
                                   @ApiParam(value = "키워드 값") @RequestParam(required = false) String keywordValue,
                                   @ApiParam(value = "정렬 타입") @RequestParam String sortType,
                                   @ApiParam(value = "페이지 행 수") @RequestParam int count,
                                   @ApiParam(value = "현재 페이지") @RequestParam int page,
                                   @ApiIgnore @AuthenticationPrincipal UserPrincipal user
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
        res.addObject(WBKey.WBModel.DefaultDataKey, productService.findProductList(paramDto));
        res.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "상품 목록 엑셀 다운로드", notes = "등록된 상품의 엑셀 다운로드 파일 저장")
    @GetMapping("/products/excel")
    public void productExcelDownload(@ApiParam(value = "전시여부") @RequestParam String[] displayFlag,
                                     @ApiParam(value = "판매상태") @RequestParam String[] status,
                                     @ApiParam(value = "조회기간 구분") @RequestParam String rangeType,
                                     @ApiParam(value = "조회기간 시작일") @RequestParam String startDay,
                                     @ApiParam(value = "조회기간 종료일") @RequestParam String endDay,
                                     @ApiParam(value = "키워드 구분") @RequestParam String keywordType,
                                     @ApiParam(value = "키워드 값") @RequestParam(required = false) String keywordValue,
                                     @ApiParam(value = "정렬 타입") @RequestParam String sortType,
                                     @ApiParam(value = "페이지 행 수") @RequestParam int count,
                                     @ApiParam(value = "현재 페이지") @RequestParam int page,
                                     @ApiIgnore @AuthenticationPrincipal UserPrincipal user,
                                     @ApiIgnore HttpServletResponse response
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
        List<ProductListDto.Response> productList = productService.findProductList(paramDto);

        if (ObjectUtils.isEmpty(productList))
            throw new WBBusinessException(ErrorCode.NO_CONTENT.getErrCode(), new String[]{"상품 목록"});

        // 엑셀 다운로드
        productService.makeExcelFile(
                productList.stream()
                        .map(ProductListDto.Response::getProductCode)
                        .collect(Collectors.toList()),
                response
        );
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "상품 등록", notes = "상품 정보 등록")
    @PostMapping("/products")
    public WBModel insertProduct(@ApiParam(value = "상품 등록 데이터") @Valid @RequestBody ProductInsertDto paramDto,
                                 @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        // 상품타입 설정
        paramDto.setProductType(user.getUserAuth().getPartnerKind());
        // 상품코드 생성
        paramDto.setProductCode(
                productUtil.generateProductCode(paramDto.getProduct().getCategoryTwoCode())
        );
        // 추가 유효성 검사
        paramDto.validProduct();

        // 상품정보 등록
        productService.insertProduct(paramDto);

        return noneDataResponse();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "상품 조회", notes = "등록된 상품 상세 정보 조회")
    @GetMapping("/products/{productCode}")
    public WBModel findProduct(@ApiParam(value = "상품코드") @PathVariable String productCode,
                               @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "상품 수정", notes = "등록된 상품 정보 수정")
    @PutMapping("/products")
    public WBModel updateProduct(@ApiParam(value = "상품 수정 데이터") @Valid @RequestBody ProductUpdateDto paramDto) {
        paramDto.setProductCode(paramDto.getProductCode());
        // 추가 유효성 검사
        paramDto.validProduct();
        // 상품정보 수정
        productService.updateProduct(paramDto);

        return noneDataResponse();
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "상품 상태 수정", notes = "등록된 상품 상태를 일괄 변경 처리 기능 제공")
    @PatchMapping("/products")
    public WBModel updateProductStatus(@ApiParam(value = "상품 상태 일괄 변경 데이터") @Valid @RequestBody StatusUpdateDto paramDto) {
        // 상품 일괄 상태 변경
        productService.updateProductStatus(paramDto);

        return noneDataResponse();
    }

}
