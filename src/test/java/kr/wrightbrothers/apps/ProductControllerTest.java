package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.constants.ProductConst;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.ProductUtil;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.apps.file.dto.FileUpdateDto;
import kr.wrightbrothers.apps.file.dto.FileUploadDto;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.file.service.S3Service;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.apps.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

class ProductControllerTest extends BaseControllerTests {

    @Autowired
    private FileService fileService;
    @Autowired
    private S3Service s3Service;
    @Autowired
    protected ProductService productService;
    @Autowired
    protected ProductUtil productUtil;

    protected ProductInsertDto productDto;

    @BeforeEach
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    void setUpTest() throws IOException {
        // 전송파일
        MockMultipartFile[] file = new MockMultipartFile[1];
        file[0] = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "<<png data>>".getBytes()
        );

        // 파일 업로드
        FileUploadDto fileUpload = fileService.uploadFile(file, RandomUtil.generateNo()).get(0);
        // 파일 S3 업로드
        fileUpload.setFileSource(s3Service.uploadFile(new File(fileUpload.getFileSource()), PartnerKey.Aws.A3.Partner_Img_Path));
        // 파일 수정
        FileUpdateDto fileUpdate = FileUpdateDto.builder()
                .fileNo(fileUpload.getFileNo())
                .fileSeq(fileUpload.getFileSeq())
                .fileSource(fileUpload.getFileSource())
                .fileSize(fileUpload.getFileSize())
                .fileOriginalName(fileUpload.getFileOriginalName())
                .fileStatus(fileUpload.getFileStatus())
                .displaySeq(1)
                .userId("test@wrightbrothers.kr")
                .build();
        dao.update("kr.wrightbrothers.apps.file.query.File.updateFile", fileUpdate, PartnerKey.WBDataBase.Alias.Admin);

        String fileNo = fileUpload.getFileNo();

        // 상품등록 데이터 가공
        productDto = ProductInsertDto.builder()
                .fileList(List.of(fileUpdate))
                .product(ProductDto.ReqBody.builder()
                        .productType("P05")
                        .categoryOneCode("B0001")
                        .categoryOneName("자전거")
                        .categoryTwoCode("BA001")
                        .categoryTwoName("로드")
                        .categoryThrCode("51794")
                        .categoryThrName("로드")
                        .productName("Propel Advanced Disc 2")
                        .brandNo("72")
                        .brandName("Giant")
                        .modelCode("958F7839DB")
                        .modelName("Propel Advanced Disc 2")
                        .modelYear("2022")
                        .productFileNo(fileNo)
                        .build())
                .basicSpec(BasicSpecDto.ReqBody.builder()
                        .salesCategoryCode("S01")
                        .drivetrainTypeCode("D06")
                        .frameMaterialCode("F03")
                        .frameSizeCode("S")
                        .brakeTypeCode("T02")
                        .purposeThemeCode("T03")
                        .wheelSizeCode("WH1")
                        .suspensionTypeCode("S04")
                        .minHeightPerson("168")
                        .maxHeightPerson("175")
                        .bikeWeight("9.2")
                        .ageList(List.of("A01"))
                        .build())
                .sellInfo(SellInfoDto.ReqBody.builder()
                        .productAmount(2900000L)
                        .displayFlag("N")
                        .discountFlag("N")
                        .discountType("2")
                        .discountAmount("0")
                        .displayFlag("N")
                        .productOptionFlag("Y")
                        .finalSellAmount(2900000L)
                        .productStatusCode(ProductConst.Status.SALE.getCode())
                        .productStockQty(1)
                        .supplyAmount(0L)
                        .build())
                .optionList(List.of(
                        OptionDto.ReqBody.builder()
                                .optionSeq(1)
                                .optionName("옵션 명")
                                .optionValue("옵션 값")
                                .optionSurcharge(3000L)
                                .optionStockQty(1)
                                .build()
                ))
                .delivery(DeliveryDto.ReqBody.builder()
                        .deliveryType("D01")
                        .deliveryBundleFlag("N")
                        .chargeType("2")
                        .chargeBase(3000)
                        .termsFreeCode("F01")
                        .termsFreeCharge(10000000L)
                        .paymentType("2")
                        .surchargeFlag("N")
                        .unstoringZipCode("06035")
                        .unstoringAddress("서울특별시 강남구 강남대로 154길 37")
                        .unstoringAddressDetail("주경빌딩 2층")
                        .returnZipCode("06035")
                        .returnAddress("서울특별시 강남구 강남대로 154길 37")
                        .returnAddressDetail("주경빌딩 1층")
                        .exchangeCharge(100000)
                        .returnCharge(1000000)
                        .returnDeliveryCompanyCode("cjgls")
                        .build())
                .infoNotice(InfoNoticeDto.ReqBody.builder()
                        .categoryCode("11")
                        .productAttribute1("Propel Advanced Disc 2")
                        .productAttribute2("S")
                        .productAttribute3("9.2")
                        .productAttribute4("재질")
                        .productAttribute5("재질구성")
                        .productAttribute6("2022")
                        .productAttribute7("05")
                        .productAttribute8("(주)라이트브라더스")
                        .productAttribute9("세부사양")
                        .productAttribute10("품질보증기준")
                        .productAttribute11("02-000-0000")
                        .build())
                .guide(GuideDto.ReqBody.builder()
                        .productDescription("상품 상세 설명..........................................................................................")
                        .productGuide("상품 안내 사항 ..............................")
                        .deliveryGuide("배송 안내 사항 ..............................")
                        .exchangeReturnGuide("교환/반품 안내 사항 ..............................")
                        .asGuide("A/S 안내 ..............................")
                        .qnaGuide("")
                        .build())
                .build();

        // 기초 데이터 초기화 처리
        productDto.setAopUserId("test@wrightbrothers.kr");
        productDto.getProduct().setPartnerCode("PT0000001");
        productDto.setProductCode(
                productUtil.generateProductCode(productDto.getProduct().getCategoryTwoCode())
        );
        // 상품 등록
        productService.insertProduct(productDto);
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("상품 목록 조회")
    void findProductList() throws Exception {
        // 조회 파라미터 빌드
        ProductListDto.Param paramDto = ProductListDto.Param.builder()
                .displayFlag(new String[]{"Y", "N"})
                .status(new String[]{
                        ProductConst.Status.PRODUCT_INSPECTION.getCode(),
                        ProductConst.Status.SALE.getCode(),
                        ProductConst.Status.RESERVATION.getCode(),
                        ProductConst.Status.SOLD_OUT.getCode(),
                        ProductConst.Status.END_OF_SALE.getCode(),
                        ProductConst.Status.REJECT_INSPECTION.getCode()
                })
                .rangeType("PRODUCT")
                .startDay(new SimpleDateFormat("yyyyMMdd").format(new Date()))
                .endDay(new SimpleDateFormat("yyyyMMdd").format(new Date()))
                .keywordType("NAME")
                .keywordValue("철새")
                .sortType("CRE")
                .build();

        // 상품 목록 조회 API 테스트
        mockMvc.perform(get("/v1/products")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                        .queryParam("displayFlag", paramDto.getDisplayFlag())
                        .queryParam("status", paramDto.getStatus())
                        .queryParam("rangeType", paramDto.getRangeType())
                        .queryParam("startDay", paramDto.getStartDay())
                        .queryParam("endDay", paramDto.getEndDay())
                        .queryParam("keywordType", paramDto.getKeywordType())
                        .queryParam("keywordValue", paramDto.getKeywordValue())
                        .queryParam("sortType", paramDto.getSortType())
                        .queryParam("count", String.valueOf(1))
                        .queryParam("page", String.valueOf(1))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("product-list",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("displayFlag").description("전시 상태").attributes(key("etc").value("Y : 노출, N : 미노출")),
                                        parameterWithName("status").description("상품 상태").attributes(key("etc").value("상품 상태 마스터 코드 000008")),
                                        parameterWithName("rangeType").description("조회 기간 구분").attributes(key("etc").value("PRODUCT 상품등록일, SALE 판매시작일")),
                                        parameterWithName("startDay").description("검색 시작 일자").attributes(key("etc").value("YYYYMMDD 예)20220913")),
                                        parameterWithName("endDay").description("검색 종료 일자").attributes(key("etc").value("YYYYMMDD 예)20220913")),
                                        parameterWithName("keywordType").description("키워드 구분").attributes(key("etc").value("NAME 상품명, CODE 상품코드")),
                                        parameterWithName("keywordValue").description("키워드 값").optional().attributes(key("etc").value("")),
                                        parameterWithName("sortType").description("정렬 구분").attributes(key("etc").value("CRE 등록일순, UPD 수정일순, HSAMT 판매가 높은순, LSAMT 판매가 낮은순")),
                                        parameterWithName("count").description("페이지 ROW 수").attributes(key("etc").value("")),
                                        parameterWithName("page").description("페이지").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).optional().description("상품 목록"),
                                        fieldWithPath("data[].productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data[].brandName").type(JsonFieldType.STRING).description("브랜드"),
                                        fieldWithPath("data[].categoryOneName").type(JsonFieldType.STRING).description("대 카테고리"),
                                        fieldWithPath("data[].categoryTwoName").type(JsonFieldType.STRING).description("중 카테고리"),
                                        fieldWithPath("data[].categoryThrName").type(JsonFieldType.STRING).optional().description("소 카테고리"),
                                        fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data[].productStatusCode").type(JsonFieldType.STRING).description("상품 상태 코드"),
                                        fieldWithPath("data[].productStatusName").type(JsonFieldType.STRING).description("상품 상태 명"),
                                        fieldWithPath("data[].displayFlag").type(JsonFieldType.STRING).description("전시 상태"),
                                        fieldWithPath("data[].displayFlagName").type(JsonFieldType.STRING).description("전시 상태 명"),
                                        fieldWithPath("data[].productStockQty").type(JsonFieldType.NUMBER).description("재고 수량"),
                                        fieldWithPath("data[].finalSellAmount").type(JsonFieldType.STRING).description("판매가"),
                                        fieldWithPath("data[].productOptionFlag").type(JsonFieldType.STRING).description("옵션 여부"),
                                        fieldWithPath("data[].createDate").type(JsonFieldType.STRING).description("등록일시"),
                                        fieldWithPath("data[].updateDate").type(JsonFieldType.STRING).description("수정일시"),
                                        fieldWithPath("data[].createUserId").type(JsonFieldType.STRING).description("등록자 아이디"),
                                        fieldWithPath("data[].createUserName").type(JsonFieldType.STRING).description("등록자 이름"),
                                        fieldWithPath("totalItems").type(JsonFieldType.NUMBER).description("전체 조회 건수"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )

                ))
                ;


    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("상품 등록")
    void insertProduct() throws Exception {
        // 상품 등록 API 테스트
        mockMvc.perform(post("/v1/products")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(productDto))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("product-insert",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("product").type(JsonFieldType.OBJECT).description("상품 기본 정보").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryOneCode").type(JsonFieldType.STRING).description("대 카테고리 코드").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryOneName").type(JsonFieldType.STRING).description("대 카테고리 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryTwoCode").type(JsonFieldType.STRING).description("중 카테고리 코드").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryTwoName").type(JsonFieldType.STRING).description("중 카테고리 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryThrCode").type(JsonFieldType.STRING).description("소 카테고리 코드").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryThrName").type(JsonFieldType.STRING).description("소 카테고리 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.productName").type(JsonFieldType.STRING).description("상품명").attributes(key("etc").value("")),
                                        fieldWithPath("product.brandNo").type(JsonFieldType.STRING).description("브랜드 번호").attributes(key("etc").value("")),
                                        fieldWithPath("product.brandName").type(JsonFieldType.STRING).description("브랜드 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.modelCode").type(JsonFieldType.STRING).description("모델 코드").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.modelName").type(JsonFieldType.STRING).description("모델 명").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.modelYear").type(JsonFieldType.STRING).description("연식").attributes(key("etc").value("공통코드 000020")),
                                        fieldWithPath("product.youtubeUrl").type(JsonFieldType.STRING).description("유튜브 URL").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.productBarcode").type(JsonFieldType.STRING).description("상품 바코드").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.modelName").type(JsonFieldType.STRING).description("모델 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.productFileNo").type(JsonFieldType.STRING).description("상품 이미지 파일 대표 번호").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec").type(JsonFieldType.OBJECT).description("기본 스펙").optional().attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.salesCategoryCode").type(JsonFieldType.STRING).description("완차구분").attributes(key("etc").value("공통코드 000009")),
                                        fieldWithPath("basicSpec.drivetrainTypeCode").type(JsonFieldType.STRING).description("구동계").attributes(key("etc").value("공통코드 000010")),
                                        fieldWithPath("basicSpec.frameMaterialCode").type(JsonFieldType.STRING).description("프레임소재").attributes(key("etc").value("공통코드 000011")),
                                        fieldWithPath("basicSpec.frameSizeCode").type(JsonFieldType.STRING).description("프레임사이즈").attributes(key("etc").value("공통코드 000012")),
                                        fieldWithPath("basicSpec.brakeTypeCode").type(JsonFieldType.STRING).description("브레이크타입").attributes(key("etc").value("공통코드 000013")),
                                        fieldWithPath("basicSpec.purposeThemeCode").type(JsonFieldType.STRING).description("용도테마").attributes(key("etc").value("공통코드 000014")),
                                        fieldWithPath("basicSpec.wheelSizeCode").type(JsonFieldType.STRING).description("휠사이즈").attributes(key("etc").value("공통코드 000015")),
                                        fieldWithPath("basicSpec.suspensionTypeCode").type(JsonFieldType.STRING).description("서스펜션").attributes(key("etc").value("공통코드 000016")),
                                        fieldWithPath("basicSpec.minHeightPerson").type(JsonFieldType.STRING).description("호환키(최소)").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.maxHeightPerson").type(JsonFieldType.STRING).description("호환키(최대)").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.bikeWeight").type(JsonFieldType.STRING).description("무게").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.ageList").type(JsonFieldType.ARRAY).description("탑승 연령대").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo").type(JsonFieldType.OBJECT).description("판매 정보").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productAmount").type(JsonFieldType.NUMBER).description("상품금액").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.discountFlag").type(JsonFieldType.STRING).description("할인 여부").attributes(key("etc").value("Y 설정, N 설정안함")),
                                        fieldWithPath("sellInfo.discountType").type(JsonFieldType.STRING).description("할인 구분").optional().attributes(key("etc").value("공통코드 000079")),
                                        fieldWithPath("sellInfo.discountAmount").type(JsonFieldType.STRING).description("할인 금액").optional().attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.supplyAmount").type(JsonFieldType.NUMBER).description("공급 금액").optional().attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.finalSellAmount").type(JsonFieldType.NUMBER).description("판매가").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productStatusCode").type(JsonFieldType.STRING).description("상품상태").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.displayFlag").type(JsonFieldType.STRING).description("전시상태").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productOptionFlag").type(JsonFieldType.STRING).description("옵션여부").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productStockQty").type(JsonFieldType.NUMBER).description("재고").attributes(key("etc").value("")),
                                        fieldWithPath("optionList[]").type(JsonFieldType.ARRAY).description("옵션 정보").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionSeq").type(JsonFieldType.NUMBER).description("옵션 번호").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionName").type(JsonFieldType.STRING).description("옵션 명").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionValue").type(JsonFieldType.STRING).description("옵션 항목").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionSurcharge").type(JsonFieldType.NUMBER).description("변동 금액").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionStockQty").type(JsonFieldType.NUMBER).description("옵션 재고 수량").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery").type(JsonFieldType.OBJECT).description("배송 정보").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.deliveryType").type(JsonFieldType.STRING).description("배송방법").attributes(key("etc").value("D01 택배/소포/등기, D07 직접배송(화물배달), D06 방문수령")),
                                        fieldWithPath("delivery.deliveryBundleFlag").type(JsonFieldType.STRING).description("묶음배송").attributes(key("etc").value("Y 가능, N 불가")),
                                        fieldWithPath("delivery.chargeType").type(JsonFieldType.STRING).description("배송비 설정").attributes(key("etc").value("공통코드 000023")),
                                        fieldWithPath("delivery.chargeBase").type(JsonFieldType.NUMBER).description("기본 배송비").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.termsFreeCode").type(JsonFieldType.STRING).description("*** (무료)배송비 기준 코드").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.termsFreeCharge").type(JsonFieldType.NUMBER).description("(무료)배송비 기준 금액").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.paymentType").type(JsonFieldType.STRING).description("결제방식").attributes(key("etc").value("1 선결제, 2 착불, 3 착불 또는 선결제")),
                                        fieldWithPath("delivery.surchargeFlag").type(JsonFieldType.STRING).description("제주/도서산간 배송비 추가 여부").attributes(key("etc").value("Y 설정, N 미설정")),
                                        fieldWithPath("delivery.areaCode").type(JsonFieldType.STRING).description("권역").optional().attributes(key("etc").value("공통코드 000080")),
                                        fieldWithPath("delivery.surchargeJejudo").type(JsonFieldType.NUMBER).description("제주도 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.surchargeIsolated").type(JsonFieldType.NUMBER).description("도서산간 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringZipCode").type(JsonFieldType.STRING).description("출고지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringAddress").type(JsonFieldType.STRING).description("출고지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringAddressDetail").type(JsonFieldType.STRING).description("출고지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnZipCode").type(JsonFieldType.STRING).description("반품지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnAddress").type(JsonFieldType.STRING).description("반품지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnAddressDetail").type(JsonFieldType.STRING).description("반품지 상세주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.exchangeCharge").type(JsonFieldType.NUMBER).description("교환배송비").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnCharge").type(JsonFieldType.NUMBER).description("반품배송비(편도)").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnDeliveryCompanyCode").type(JsonFieldType.STRING).description("반품/교환 택배사 코드").attributes(key("etc").value("공통코드 000044")),
                                        fieldWithPath("infoNotice").type(JsonFieldType.OBJECT).description("상품 정보 고시").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.categoryCode").type(JsonFieldType.STRING).description("상품 구분").attributes(key("etc").value("공통코드 000033")),
                                        fieldWithPath("infoNotice.productAttribute1").type(JsonFieldType.STRING).description("가변필드 1").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute2").type(JsonFieldType.STRING).description("가변필드 2").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute3").type(JsonFieldType.STRING).description("가변필드 3").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute4").type(JsonFieldType.STRING).description("가변필드 4").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute5").type(JsonFieldType.STRING).description("가변필드 5").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute6").type(JsonFieldType.STRING).description("가변필드 6").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute7").type(JsonFieldType.STRING).description("가변필드 7").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute8").type(JsonFieldType.STRING).description("가변필드 8").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute9").type(JsonFieldType.STRING).description("가변필드 9").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute10").type(JsonFieldType.STRING).description("가변필드 10").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute11").type(JsonFieldType.STRING).description("가변필드 11").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute12").type(JsonFieldType.STRING).description("가변필드 12").optional().attributes(key("etc").value("")),
                                        fieldWithPath("guide").type(JsonFieldType.OBJECT).description("안내 정보").attributes(key("etc").value("")),
                                        fieldWithPath("guide.productDescription").type(JsonFieldType.STRING).description("상품 상세 설명").attributes(key("etc").value("")),
                                        fieldWithPath("guide.productGuide").type(JsonFieldType.STRING).description("상품 안내 사항").attributes(key("etc").value("")),
                                        fieldWithPath("guide.deliveryGuide").type(JsonFieldType.STRING).description("배송 안내 사항").attributes(key("etc").value("")),
                                        fieldWithPath("guide.exchangeReturnGuide").type(JsonFieldType.STRING).description("교환/반품 안내 사항").optional().attributes(key("etc").value("")),
                                        fieldWithPath("guide.asGuide").type(JsonFieldType.STRING).description("A/S 안내").attributes(key("etc").value("")),
                                        fieldWithPath("guide.qnaGuide").type(JsonFieldType.STRING).description("자주 묻는 질문").optional().attributes(key("etc").value("")),
                                        fieldWithPath("fileList[]").type(JsonFieldType.ARRAY).description("상품 이미지 파일").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileNo").type(JsonFieldType.STRING).description("파일 대표 번호").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileSeq").type(JsonFieldType.NUMBER).description("파일 번호").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileSource").type(JsonFieldType.STRING).description("파일 경로").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileSize").type(JsonFieldType.STRING).description("파일 사이즈").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileOriginalName").type(JsonFieldType.STRING).description("원본 파일 이름").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileStatus").type(JsonFieldType.STRING).description("파일 상태").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileDescription").type(JsonFieldType.STRING).description("파일 설명").optional().attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].displaySeq").type(JsonFieldType.NUMBER).description("디스플레이 순서").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].tifPath").type(JsonFieldType.STRING).description("TIF 파일 경로").optional().attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("상품 상세 조회")
    void findProduct() throws Exception {


        // 상품 상세 API 조회
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/products/{productCode}", productDto.getProduct().getProductCode())
                .header(AUTH_HEADER, JWT_TOKEN)
                .contentType(MediaType.TEXT_HTML)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("product-find",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("productCode").description("상품 코드")
                                ),
                                responseFields(
                                        fieldWithPath("data.product").type(JsonFieldType.OBJECT).description("상품 기본 정보"),
                                        fieldWithPath("data.product.productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data.product.productType").type(JsonFieldType.STRING).description("상품 유형"),
                                        fieldWithPath("data.product.categoryOneCode").type(JsonFieldType.STRING).description("대 카테고리 코드"),
                                        fieldWithPath("data.product.categoryOneName").type(JsonFieldType.STRING).description("대 카테고리 명"),
                                        fieldWithPath("data.product.categoryTwoCode").type(JsonFieldType.STRING).description("중 카테고리 코드"),
                                        fieldWithPath("data.product.categoryTwoName").type(JsonFieldType.STRING).description("중 카테고리 명"),
                                        fieldWithPath("data.product.categoryThrCode").type(JsonFieldType.STRING).description("소 카테고리 코드"),
                                        fieldWithPath("data.product.categoryThrName").type(JsonFieldType.STRING).description("소 카테고리 명"),
                                        fieldWithPath("data.product.productName").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.product.brandNo").type(JsonFieldType.STRING).description("브랜드 번호"),
                                        fieldWithPath("data.product.brandName").type(JsonFieldType.STRING).description("브랜드 명"),
                                        fieldWithPath("data.product.modelCode").type(JsonFieldType.STRING).description("모델 코드"),
                                        fieldWithPath("data.product.modelName").type(JsonFieldType.STRING).description("모델 명"),
                                        fieldWithPath("data.product.modelYear").type(JsonFieldType.STRING).description("연식"),
                                        fieldWithPath("data.product.youtubeUrl").type(JsonFieldType.STRING).description("유튜브 URL").optional(),
                                        fieldWithPath("data.product.productBarcode").type(JsonFieldType.STRING).description("상품 바코드").optional(),
                                        fieldWithPath("data.product.productFileNo").type(JsonFieldType.STRING).description("상품 이미지 파일 대표 번호"),
                                        fieldWithPath("data.basicSpec").type(JsonFieldType.OBJECT).description("기본 스펙").optional(),
                                        fieldWithPath("data.basicSpec.salesCategoryCode").type(JsonFieldType.STRING).description("완차구분"),
                                        fieldWithPath("data.basicSpec.drivetrainTypeCode").type(JsonFieldType.STRING).description("구동계"),
                                        fieldWithPath("data.basicSpec.frameMaterialCode").type(JsonFieldType.STRING).description("프레임소재"),
                                        fieldWithPath("data.basicSpec.frameSizeCode").type(JsonFieldType.STRING).description("프레임사이즈"),
                                        fieldWithPath("data.basicSpec.brakeTypeCode").type(JsonFieldType.STRING).description("브레이크타입"),
                                        fieldWithPath("data.basicSpec.purposeThemeCode").type(JsonFieldType.STRING).description("용도테마"),
                                        fieldWithPath("data.basicSpec.wheelSizeCode").type(JsonFieldType.STRING).description("휠사이즈"),
                                        fieldWithPath("data.basicSpec.suspensionTypeCode").type(JsonFieldType.STRING).description("서스펜션"),
                                        fieldWithPath("data.basicSpec.minHeightPerson").type(JsonFieldType.STRING).description("호환키(최소)"),
                                        fieldWithPath("data.basicSpec.maxHeightPerson").type(JsonFieldType.STRING).description("호환키(최대)"),
                                        fieldWithPath("data.basicSpec.bikeWeight").type(JsonFieldType.STRING).description("무게"),
                                        fieldWithPath("data.basicSpec.ageList").type(JsonFieldType.ARRAY).description("탑승 연령대"),
                                        fieldWithPath("data.sellInfo").type(JsonFieldType.OBJECT).description("판매 정보"),
                                        fieldWithPath("data.sellInfo.productAmount").type(JsonFieldType.NUMBER).description("상품금액"),
                                        fieldWithPath("data.sellInfo.discountFlag").type(JsonFieldType.STRING).description("할인 여부"),
                                        fieldWithPath("data.sellInfo.discountType").type(JsonFieldType.STRING).description("할인 구분").optional(),
                                        fieldWithPath("data.sellInfo.discountAmount").type(JsonFieldType.STRING).description("할인 금액").optional(),
                                        fieldWithPath("data.sellInfo.supplyAmount").type(JsonFieldType.NUMBER).description("공급 금액").optional(),
                                        fieldWithPath("data.sellInfo.finalSellAmount").type(JsonFieldType.NUMBER).description("판매가"),
                                        fieldWithPath("data.sellInfo.productStatusCode").type(JsonFieldType.STRING).description("상품상태"),
                                        fieldWithPath("data.sellInfo.displayFlag").type(JsonFieldType.STRING).description("전시상태"),
                                        fieldWithPath("data.sellInfo.productOptionFlag").type(JsonFieldType.STRING).description("옵션여부"),
                                        fieldWithPath("data.sellInfo.productStockQty").type(JsonFieldType.NUMBER).description("재고"),
                                        fieldWithPath("data.sellInfo.productSellStartDate").type(JsonFieldType.STRING).description("* 판매 시작 일시").optional(),
                                        fieldWithPath("data.sellInfo.productSellEndDate").type(JsonFieldType.STRING).description("* 판매 종료 일시").optional(),
                                        fieldWithPath("data.optionList[]").type(JsonFieldType.ARRAY).description("옵션 정보").optional(),
                                        fieldWithPath("data.optionList[].optionSeq").type(JsonFieldType.NUMBER).description("옵션 번호").optional(),
                                        fieldWithPath("data.optionList[].optionName").type(JsonFieldType.STRING).description("옵션 명").optional(),
                                        fieldWithPath("data.optionList[].optionValue").type(JsonFieldType.STRING).description("옵션 항목").optional(),
                                        fieldWithPath("data.optionList[].optionSurcharge").type(JsonFieldType.NUMBER).description("변동 금액").optional(),
                                        fieldWithPath("data.optionList[].optionStockQty").type(JsonFieldType.NUMBER).description("옵션 재고 수량").optional(),
                                        fieldWithPath("data.delivery").type(JsonFieldType.OBJECT).description("배송 정보"),
                                        fieldWithPath("data.delivery.deliveryType").type(JsonFieldType.STRING).description("배송방법"),
                                        fieldWithPath("data.delivery.deliveryBundleFlag").type(JsonFieldType.STRING).description("묶음배송"),
                                        fieldWithPath("data.delivery.chargeType").type(JsonFieldType.STRING).description("배송비 설정"),
                                        fieldWithPath("data.delivery.chargeBase").type(JsonFieldType.NUMBER).description("기본 배송비"),
                                        fieldWithPath("data.delivery.termsFreeCode").type(JsonFieldType.STRING).description("*** (무료)배송비 기준 코드").optional(),
                                        fieldWithPath("data.delivery.termsFreeCharge").type(JsonFieldType.NUMBER).description("(무료)배송비 기준 금액"),
                                        fieldWithPath("data.delivery.paymentType").type(JsonFieldType.STRING).description("결제방식"),
                                        fieldWithPath("data.delivery.surchargeFlag").type(JsonFieldType.STRING).description("제주/도서산간 배송비 추가 여부"),
                                        fieldWithPath("data.delivery.areaCode").type(JsonFieldType.STRING).description("권역").optional(),
                                        fieldWithPath("data.delivery.surchargeJejudo").type(JsonFieldType.NUMBER).description("제주도 추가 배송비").optional(),
                                        fieldWithPath("data.delivery.surchargeIsolated").type(JsonFieldType.NUMBER).description("도서산간 추가 배송비").optional(),
                                        fieldWithPath("data.delivery.unstoringZipCode").type(JsonFieldType.STRING).description("출고지 우편번호"),
                                        fieldWithPath("data.delivery.unstoringAddress").type(JsonFieldType.STRING).description("출고지 주소"),
                                        fieldWithPath("data.delivery.unstoringAddressDetail").type(JsonFieldType.STRING).description("출고지 상세주소"),
                                        fieldWithPath("data.delivery.returnZipCode").type(JsonFieldType.STRING).description("반품지 우편번호"),
                                        fieldWithPath("data.delivery.returnAddress").type(JsonFieldType.STRING).description("반품지 주소"),
                                        fieldWithPath("data.delivery.returnAddressDetail").type(JsonFieldType.STRING).description("반품지 상세주소"),
                                        fieldWithPath("data.delivery.exchangeCharge").type(JsonFieldType.NUMBER).description("교환배송비"),
                                        fieldWithPath("data.delivery.returnCharge").type(JsonFieldType.NUMBER).description("반품배송비(편도)"),
                                        fieldWithPath("data.delivery.returnDeliveryCompanyCode").type(JsonFieldType.STRING).description("반품/교환 택배사 코드"),
                                        fieldWithPath("data.infoNotice").type(JsonFieldType.OBJECT).description("상품 정보 고시"),
                                        fieldWithPath("data.infoNotice.categoryCode").type(JsonFieldType.STRING).description("상품 구분"),
                                        fieldWithPath("data.infoNotice.productAttribute1").type(JsonFieldType.STRING).description("정보고시 가변필드 1").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute2").type(JsonFieldType.STRING).description("정보고시 가변필드 2").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute3").type(JsonFieldType.STRING).description("정보고시 가변필드 3").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute4").type(JsonFieldType.STRING).description("정보고시 가변필드 4").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute5").type(JsonFieldType.STRING).description("정보고시 가변필드 5").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute6").type(JsonFieldType.STRING).description("정보고시 가변필드 6").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute7").type(JsonFieldType.STRING).description("정보고시 가변필드 7").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute8").type(JsonFieldType.STRING).description("정보고시 가변필드 8").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute9").type(JsonFieldType.STRING).description("정보고시 가변필드 9").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute10").type(JsonFieldType.STRING).description("정보고시 가변필드 10").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute11").type(JsonFieldType.STRING).description("정보고시 가변필드 11").optional(),
                                        fieldWithPath("data.infoNotice.productAttribute12").type(JsonFieldType.STRING).description("정보고시 가변필드 12").optional(),
                                        fieldWithPath("data.guide").type(JsonFieldType.OBJECT).description("안내 정보"),
                                        fieldWithPath("data.guide.productDescription").type(JsonFieldType.STRING).description("상품 상세 설명"),
                                        fieldWithPath("data.guide.productGuide").type(JsonFieldType.STRING).description("상품 안내 사항"),
                                        fieldWithPath("data.guide.deliveryGuide").type(JsonFieldType.STRING).description("배송 안내 사항"),
                                        fieldWithPath("data.guide.exchangeReturnGuide").type(JsonFieldType.STRING).description("교환/반품 안내 사항").optional(),
                                        fieldWithPath("data.guide.asGuide").type(JsonFieldType.STRING).description("A/S 안내"),
                                        fieldWithPath("data.guide.qnaGuide").type(JsonFieldType.STRING).description("자주 묻는 질문"),
                                        fieldWithPath("data.rejectReason").type(JsonFieldType.STRING).description("* 검수 반려 사유").optional(),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )

                ))
                ;

    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("상품 수정")
    void updateProduct() throws Exception {
        // 상품 등록 API 테스트
        mockMvc.perform(put("/v1/products")
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                generateUpdateProductData(
                                        productDto.getProduct().getProductCode(),
                                        productDto.getFileList().get(0),
                                        productDto.getProduct().getProductFileNo()
                                )))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("product-update",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("productCode").type(JsonFieldType.STRING).description("상품 코드").attributes(key("etc").value("")),
                                        fieldWithPath("product").type(JsonFieldType.OBJECT).description("상품 기본 정보").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryOneCode").type(JsonFieldType.STRING).description("대 카테고리 코드").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryOneName").type(JsonFieldType.STRING).description("대 카테고리 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryTwoCode").type(JsonFieldType.STRING).description("중 카테고리 코드").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryTwoName").type(JsonFieldType.STRING).description("중 카테고리 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryThrCode").type(JsonFieldType.STRING).description("소 카테고리 코드").attributes(key("etc").value("")),
                                        fieldWithPath("product.categoryThrName").type(JsonFieldType.STRING).description("소 카테고리 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.productName").type(JsonFieldType.STRING).description("상품명").attributes(key("etc").value("")),
                                        fieldWithPath("product.brandNo").type(JsonFieldType.STRING).description("브랜드 번호").attributes(key("etc").value("")),
                                        fieldWithPath("product.brandName").type(JsonFieldType.STRING).description("브랜드 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.modelCode").type(JsonFieldType.STRING).description("모델 코드").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.modelName").type(JsonFieldType.STRING).description("모델 명").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.modelYear").type(JsonFieldType.STRING).description("연식").attributes(key("etc").value("공통코드 000020")),
                                        fieldWithPath("product.youtubeUrl").type(JsonFieldType.STRING).description("유튜브 URL").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.productBarcode").type(JsonFieldType.STRING).description("상품 바코드").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.modelName").type(JsonFieldType.STRING).description("모델 명").attributes(key("etc").value("")),
                                        fieldWithPath("product.productFileNo").type(JsonFieldType.STRING).description("상품 이미지 파일 대표 번호").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec").type(JsonFieldType.OBJECT).description("기본 스펙").optional().attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.salesCategoryCode").type(JsonFieldType.STRING).description("완차구분").attributes(key("etc").value("공통코드 000009")),
                                        fieldWithPath("basicSpec.drivetrainTypeCode").type(JsonFieldType.STRING).description("구동계").attributes(key("etc").value("공통코드 000010")),
                                        fieldWithPath("basicSpec.frameMaterialCode").type(JsonFieldType.STRING).description("프레임소재").attributes(key("etc").value("공통코드 000011")),
                                        fieldWithPath("basicSpec.frameSizeCode").type(JsonFieldType.STRING).description("프레임사이즈").attributes(key("etc").value("공통코드 000012")),
                                        fieldWithPath("basicSpec.brakeTypeCode").type(JsonFieldType.STRING).description("브레이크타입").attributes(key("etc").value("공통코드 000013")),
                                        fieldWithPath("basicSpec.purposeThemeCode").type(JsonFieldType.STRING).description("용도테마").attributes(key("etc").value("공통코드 000014")),
                                        fieldWithPath("basicSpec.wheelSizeCode").type(JsonFieldType.STRING).description("휠사이즈").attributes(key("etc").value("공통코드 000015")),
                                        fieldWithPath("basicSpec.suspensionTypeCode").type(JsonFieldType.STRING).description("서스펜션").attributes(key("etc").value("공통코드 000016")),
                                        fieldWithPath("basicSpec.minHeightPerson").type(JsonFieldType.STRING).description("호환키(최소)").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.maxHeightPerson").type(JsonFieldType.STRING).description("호환키(최대)").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.bikeWeight").type(JsonFieldType.STRING).description("무게").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.ageList").type(JsonFieldType.ARRAY).description("탑승 연령대").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo").type(JsonFieldType.OBJECT).description("판매 정보").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productAmount").type(JsonFieldType.NUMBER).description("상품금액").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.discountFlag").type(JsonFieldType.STRING).description("할인 여부").attributes(key("etc").value("Y 설정, N 설정안함")),
                                        fieldWithPath("sellInfo.discountType").type(JsonFieldType.STRING).description("할인 구분").optional().attributes(key("etc").value("공통코드 000079")),
                                        fieldWithPath("sellInfo.discountAmount").type(JsonFieldType.STRING).description("할인 금액").optional().attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.supplyAmount").type(JsonFieldType.NUMBER).description("공급 금액").optional().attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.finalSellAmount").type(JsonFieldType.NUMBER).description("판매가").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productStatusCode").type(JsonFieldType.STRING).description("상품상태").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.displayFlag").type(JsonFieldType.STRING).description("전시상태").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productOptionFlag").type(JsonFieldType.STRING).description("옵션여부").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productStockQty").type(JsonFieldType.NUMBER).description("재고").attributes(key("etc").value("")),
                                        fieldWithPath("optionList[]").type(JsonFieldType.ARRAY).description("옵션 정보").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionSeq").type(JsonFieldType.NUMBER).description("옵션 번호").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionName").type(JsonFieldType.STRING).description("옵션 명").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionValue").type(JsonFieldType.STRING).description("옵션 항목").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionSurcharge").type(JsonFieldType.NUMBER).description("변동 금액").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionStockQty").type(JsonFieldType.NUMBER).description("옵션 재고 수량").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery").type(JsonFieldType.OBJECT).description("배송 정보").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.deliveryType").type(JsonFieldType.STRING).description("배송방법").attributes(key("etc").value("D01 택배/소포/등기, D07 직접배송(화물배달), D06 방문수령")),
                                        fieldWithPath("delivery.deliveryBundleFlag").type(JsonFieldType.STRING).description("묶음배송").attributes(key("etc").value("Y 가능, N 불가")),
                                        fieldWithPath("delivery.chargeType").type(JsonFieldType.STRING).description("배송비 설정").attributes(key("etc").value("공통코드 000023")),
                                        fieldWithPath("delivery.chargeBase").type(JsonFieldType.NUMBER).description("기본 배송비").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.termsFreeCode").type(JsonFieldType.STRING).description("*** (무료)배송비 기준 코드").attributes(key("etc").value("")).optional(),
                                        fieldWithPath("delivery.termsFreeCharge").type(JsonFieldType.NUMBER).description("(무료)배송비 기준 금액").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.paymentType").type(JsonFieldType.STRING).description("결제방식").attributes(key("etc").value("1 선결제, 2 착불, 3 착불 또는 선결제")),
                                        fieldWithPath("delivery.surchargeFlag").type(JsonFieldType.STRING).description("제주/도서산간 배송비 추가 여부").attributes(key("etc").value("Y 설정, N 미설정")),
                                        fieldWithPath("delivery.areaCode").type(JsonFieldType.STRING).description("권역").optional().attributes(key("etc").value("공통코드 000080")),
                                        fieldWithPath("delivery.surchargeJejudo").type(JsonFieldType.NUMBER).description("제주도 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.surchargeIsolated").type(JsonFieldType.NUMBER).description("도서산간 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringZipCode").type(JsonFieldType.STRING).description("출고지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringAddress").type(JsonFieldType.STRING).description("출고지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringAddressDetail").type(JsonFieldType.STRING).description("출고지 상세주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnZipCode").type(JsonFieldType.STRING).description("반품지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnAddress").type(JsonFieldType.STRING).description("반품지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnAddressDetail").type(JsonFieldType.STRING).description("반품지 상세주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.exchangeCharge").type(JsonFieldType.NUMBER).description("교환배송비").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnCharge").type(JsonFieldType.NUMBER).description("반품배송비(편도)").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnDeliveryCompanyCode").type(JsonFieldType.STRING).description("반품/교환 택배사 코드").attributes(key("etc").value("공통코드 000044")),
                                        fieldWithPath("infoNotice").type(JsonFieldType.OBJECT).description("상품 정보 고시").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.categoryCode").type(JsonFieldType.STRING).description("상품 구분").attributes(key("etc").value("공통코드 000033")),
                                        fieldWithPath("infoNotice.productAttribute1").type(JsonFieldType.STRING).description("정보고시 가변필드1").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute2").type(JsonFieldType.STRING).description("정보고시 가변필드2").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute3").type(JsonFieldType.STRING).description("정보고시 가변필드3").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute4").type(JsonFieldType.STRING).description("정보고시 가변필드4").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute5").type(JsonFieldType.STRING).description("정보고시 가변필드5").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute6").type(JsonFieldType.STRING).description("정보고시 가변필드6").attributes(key("etc").value("공통코드 000031")),
                                        fieldWithPath("infoNotice.productAttribute7").type(JsonFieldType.STRING).description("정보고시 가변필드7").attributes(key("etc").value("공통코드 000032")),
                                        fieldWithPath("infoNotice.productAttribute8").type(JsonFieldType.STRING).description("정보고시 가변필드8").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute9").type(JsonFieldType.STRING).description("정보고시 가변필드9").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute10").type(JsonFieldType.STRING).description("정보고시 가변필드10").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute11").type(JsonFieldType.STRING).description("정보고시 가변필드11").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productAttribute12").type(JsonFieldType.STRING).description("정보고시 가변필드12").optional().attributes(key("etc").value("")),
                                        fieldWithPath("guide").type(JsonFieldType.OBJECT).description("안내 정보").attributes(key("etc").value("")),
                                        fieldWithPath("guide.productDescription").type(JsonFieldType.STRING).description("상품 상세 설명").attributes(key("etc").value("")),
                                        fieldWithPath("guide.productGuide").type(JsonFieldType.STRING).description("상품 안내 사항").attributes(key("etc").value("")),
                                        fieldWithPath("guide.deliveryGuide").type(JsonFieldType.STRING).description("배송 안내 사항").attributes(key("etc").value("")),
                                        fieldWithPath("guide.exchangeReturnGuide").type(JsonFieldType.STRING).description("교환/반품 안내 사항").optional().attributes(key("etc").value("")),
                                        fieldWithPath("guide.asGuide").type(JsonFieldType.STRING).description("A/S 안내").attributes(key("etc").value("")),
                                        fieldWithPath("guide.qnaGuide").type(JsonFieldType.STRING).description("자주 묻는 질문").optional().attributes(key("etc").value("")),
                                        fieldWithPath("fileList[]").type(JsonFieldType.ARRAY).description("상품 이미지 파일").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileNo").type(JsonFieldType.STRING).description("파일 대표 번호").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileSeq").type(JsonFieldType.NUMBER).description("파일 번호").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileSource").type(JsonFieldType.STRING).description("파일 경로").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileSize").type(JsonFieldType.STRING).description("파일 사이즈").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileOriginalName").type(JsonFieldType.STRING).description("원본 파일 이름").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileStatus").type(JsonFieldType.STRING).description("파일 상태").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].fileDescription").type(JsonFieldType.STRING).description("파일 설명").optional().attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].displaySeq").type(JsonFieldType.NUMBER).description("디스플레이 순서").attributes(key("etc").value("")),
                                        fieldWithPath("fileList[].tifPath").type(JsonFieldType.STRING).description("TIF 파일 경로").optional().attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                        ))
        ;
    }

    ProductUpdateDto generateUpdateProductData(String productCode, FileUpdateDto fileUpdateDto, String fileNo) {
        // 상품등록 데이터 가공
        return ProductUpdateDto.builder()
                .productCode(productCode)
                .fileList(List.of(fileUpdateDto))
                .product(ProductDto.ReqBody.builder()
                        .productType("P05")
                        .categoryOneCode("B0001")
                        .categoryOneName("자전거")
                        .categoryTwoCode("BA001")
                        .categoryTwoName("로드")
                        .categoryThrCode("51794")
                        .categoryThrName("로드")
                        .productName("Propel Advanced Disc 2")
                        .brandNo("72")
                        .brandName("Giant")
                        .modelCode("958F7839DB")
                        .modelName("Propel Advanced Disc 2")
                        .modelYear("2022")
                        .productFileNo(fileNo)
                        .build())
                .basicSpec(BasicSpecDto.ReqBody.builder()
                        .salesCategoryCode("S01")
                        .drivetrainTypeCode("D06")
                        .frameMaterialCode("F03")
                        .frameSizeCode("S")
                        .brakeTypeCode("T02")
                        .purposeThemeCode("T03")
                        .wheelSizeCode("WH1")
                        .suspensionTypeCode("S04")
                        .minHeightPerson("168")
                        .maxHeightPerson("175")
                        .bikeWeight("9.2")
                        .ageList(List.of("A01"))
                        .build())
                .sellInfo(SellInfoDto.ReqBody.builder()
                        .productAmount(2900000L)
                        .displayFlag("N")
                        .discountFlag("N")
                        .discountType("2")
                        .discountAmount("0")
                        .displayFlag("N")
                        .productOptionFlag("Y")
                        .finalSellAmount(2900000L)
                        .productStatusCode(ProductConst.Status.PRODUCT_INSPECTION.getCode())
                        .productStockQty(1)
                        .build())
                .optionList(List.of(OptionDto.ReqBody.builder()
                        .optionSeq(1)
                        .optionName("색상")
                        .optionValue("기본")
                        .optionSurcharge(0L)
                        .optionStockQty(1)
                        .build()))
                .delivery(DeliveryDto.ReqBody.builder()
                        .deliveryType("D01")
                        .deliveryBundleFlag("N")
                        .chargeType("2")
                        .chargeBase(100000)
                        .termsFreeCharge(10000000L)
                        .paymentType("2")
                        .surchargeFlag("N")
                        .unstoringZipCode("06035")
                        .unstoringAddress("서울특별시 강남구 강남대로 154길 37")
                        .unstoringAddressDetail("주경빌딩 2층")
                        .returnZipCode("06035")
                        .returnAddress("서울특별시 강남구 강남대로 154길 37")
                        .returnAddressDetail("주경빌딩 1층")
                        .exchangeCharge(10000)
                        .returnCharge(1000000)
                        .returnDeliveryCompanyCode("cjgls")
                        .build())
                .infoNotice(InfoNoticeDto.ReqBody.builder()
                        .categoryCode("11")
                        .productAttribute1("Propel Advanced Disc 1231231231232")
                        .productAttribute2("S")
                        .productAttribute3("9.2")
                        .productAttribute4("재질")
                        .productAttribute5("재질구성")
                        .productAttribute6("2022")
                        .productAttribute7("05")
                        .productAttribute8("(주)라이트브라더스")
                        .productAttribute9("세부사양")
                        .productAttribute10("품질보증기준")
                        .productAttribute11("02-000-0000")
                        .build())
                .guide(GuideDto.ReqBody.builder()
                        .productDescription("상품 상세 설명 ..............................")
                        .productGuide("상품 안내 사항 ..............................")
                        .deliveryGuide("배송 안내 사항 ..............................")
                        .exchangeReturnGuide("교환/반품 안내 사항 ..............................")
                        .asGuide("A/S 안내 ..............................")
                        .qnaGuide("")
                        .build())
                .build();
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("상품 상태 일괄 변경")
    void updateProductStatus() throws Exception {
        // 변경 요청 바디 생성
        StatusUpdateDto statusParam = StatusUpdateDto.builder()
                .productCodeList(new String[]{productDto.getProduct().getProductCode()})
                .statusType("DP")
                .statusValue("Y")
                .build();

        // 일괄 변경 API 테스트
        mockMvc.perform(patch("/v1/products")
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(statusParam))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("product-status",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("productCodeList").type(JsonFieldType.ARRAY).description("변경 상품 코드").attributes(key("etc").value("")),
                                        fieldWithPath("statusType").type(JsonFieldType.STRING).description("상품 변경 구분").attributes(key("etc").value("DP 노출 여부, ST 상품 상태 코드")),
                                        fieldWithPath("statusValue").type(JsonFieldType.STRING).description("상품 변경 값").attributes(key("etc").value("DP(Y 노출, N 미노출), ST(S01 판매시작, S08 판매종료, S02 예약중)"))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
        ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("상품(검수) 삭제")
    void deleteProduct() throws Exception {
        // 상품 등록
        productDto.setProductCode(
                productUtil.generateProductCode(productDto.getProduct().getCategoryTwoCode())
        );
        productDto.getSellInfo().setProductStatusCode(ProductConst.Status.REJECT_INSPECTION.getCode());
        productService.insertProduct(productDto);

        // 삭제 API 테스트
        mockMvc.perform(delete("/v1/products")
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.TEXT_HTML)
                        .queryParam("productCodeList", productDto.getProduct().getProductCode())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("product-delete",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("productCodeList").description("상품 코드").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                        )
                )
                ;

        // 검증
        ProductDto.ResBody findProduct = dao.selectOne("kr.wrightbrothers.apps.product.query.Product.findProduct", productDto.getProduct().getProductCode(), PartnerKey.WBDataBase.Alias.Admin);
        assertNull(findProduct);
    }

}
