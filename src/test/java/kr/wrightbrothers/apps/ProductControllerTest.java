package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.apps.file.dto.FileUpdateDto;
import kr.wrightbrothers.apps.file.dto.FileUploadDto;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.file.service.S3Service;
import kr.wrightbrothers.apps.product.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

class ProductControllerTest extends BaseControllerTests {

    @Autowired
    private FileService fileService;
    @Autowired
    private S3Service s3Service;

    private FileUpdateDto fileUpdate;
    private String fileNo;

    @BeforeEach
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    void setUpFile() throws IOException {
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
        fileUpdate = FileUpdateDto.builder()
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

        fileNo = fileUpload.getFileNo();
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("상품 목록 조회")
    void findProductList() throws Exception {
        // 조회 파라미터 빌드
        ProductListDto.Param paramDto = ProductListDto.Param.builder()
                .displayFlag(new String[]{"Y", "N"})
                .status(new String[]{
                        ProductStatusCode.PRODUCT_INSPECTION.getCode(),
                        ProductStatusCode.SALE.getCode(),
                        ProductStatusCode.RESERVATION.getCode(),
                        ProductStatusCode.SOLD_OUT.getCode(),
                        ProductStatusCode.END_OF_SALE.getCode(),
                        ProductStatusCode.REJECT_INSPECTION.getCode()
                })
                .rangeType("PRODUCT")
                .startDay(new SimpleDateFormat("yyyyMMdd").format(new Date()))
                .endDay(new SimpleDateFormat("yyyyMMdd").format(new Date()))
                .keywordType("NAME")
                .keywordValue("")
                .build();

        // 상품 목록 조회 API 테스트
        mockMvc.perform(get("/products")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                        .queryParam("displayFlag", paramDto.getDisplayFlag())
                        .queryParam("status", paramDto.getStatus())
                        .queryParam("rangeType", paramDto.getRangeType())
                        .queryParam("startDay", paramDto.getStartDay())
                        .queryParam("endDay", paramDto.getEndDay())
                        .queryParam("keywordType", paramDto.getKeywordType())
                        .queryParam("keywordValue", paramDto.getKeywordValue())
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
                                        fieldWithPath("data[].productStatus").type(JsonFieldType.STRING).description("상품 상태"),
                                        fieldWithPath("data[].displayFlag").type(JsonFieldType.STRING).description("전시 상태 "),
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
        // 등록 파라미터 데이터 정의
        ProductInsertDto productDto = ProductInsertDto.builder()
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
                        .productDescription("상품 상세 설명")
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
                        .finalSellAmount(2900000L)
                        .productStatusCode(ProductStatusCode.PRODUCT_INSPECTION.getCode())
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
                        .visitFlag("N")
                        .quickServiceFlag("N")
                        .deliveryBundleFlag("N")
                        .chargeType("CT2")
                        .chargeBase(3000)
                        .termsFreeCharge(10000000L)
                        .paymentType("P02")
                        .surchargeFlag("N")
                        .unstoringAddress("서울특별시 강남구 강남대로 154길 37, 주경빌딩 2층 (06035)")
                        .returnAddress("서울특별시 강남구 강남대로 154길 37, 주경빌딩 1층 (06035)")
                        .returnCharge(1000000)
                        .returnDeliveryCompanyCode("cjgls")
                        .build())
                .infoNotice(InfoNoticeDto.ReqBody.builder()
                        .categoryCode("11")
                        .modelName("Propel Advanced Disc 2")
                        .productSize("S")
                        .productWeight("9.2")
                        .modelYear("2022")
                        .modelMonth("05")
                        .productMfr("(주)라이트브라더스")
                        .asPhone("02-000-0000")
                        .build())
                .guide(GuideDto.ReqBody.builder()
                        .productGuide("상품 안내 사항")
                        .deliveryGuide("배송 안내 사항")
                        .exchangeReturnGuide("교환/반품 안내 사항")
                        .asGuide("A/S 안내")
                        .build())
                .build();

        // 상품 등록 API 테스트
        mockMvc.perform(post("/products")
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
                                        fieldWithPath("product.productType").type(JsonFieldType.STRING).description("상품 유형").attributes(key("etc").value("P05 기본값")),
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
                                        fieldWithPath("product.modelYear").type(JsonFieldType.STRING).description("연식").attributes(key("etc").value("")),
                                        fieldWithPath("product.youtubeUrl").type(JsonFieldType.STRING).description("유튜브 URL").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.productBarcode").type(JsonFieldType.STRING).description("상품 바코드").optional().attributes(key("etc").value("")),
                                        fieldWithPath("product.modelName").type(JsonFieldType.STRING).description("모델 명").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec").type(JsonFieldType.OBJECT).description("기본 스펙").optional().attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.salesCategoryCode").type(JsonFieldType.STRING).description("완차구분").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.drivetrainTypeCode").type(JsonFieldType.STRING).description("구동계").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.frameMaterialCode").type(JsonFieldType.STRING).description("프레임소재").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.frameSizeCode").type(JsonFieldType.STRING).description("프레임사이즈").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.brakeTypeCode").type(JsonFieldType.STRING).description("브레이크타입").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.purposeThemeCode").type(JsonFieldType.STRING).description("용도테마").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.wheelSizeCode").type(JsonFieldType.STRING).description("휠사이즈").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.suspensionTypeCode").type(JsonFieldType.STRING).description("서스펜션").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.minHeightPerson").type(JsonFieldType.STRING).description("호환키(최소)").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.maxHeightPerson").type(JsonFieldType.STRING).description("호환키(최대)").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.bikeWeight").type(JsonFieldType.STRING).description("무게").attributes(key("etc").value("")),
                                        fieldWithPath("basicSpec.ageList").type(JsonFieldType.ARRAY).description("탑승 연령대").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo").type(JsonFieldType.OBJECT).description("판매 정보").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productAmount").type(JsonFieldType.NUMBER).description("상품금액").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.discountFlag").type(JsonFieldType.STRING).description("할인 여부").attributes(key("etc").value("Y 설정, N 설정안함")),
                                        fieldWithPath("sellInfo.discountType").type(JsonFieldType.STRING).description("할인 구분").optional().attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.discountAmount").type(JsonFieldType.STRING).description("할인 금액").optional().attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.finalSellAmount").type(JsonFieldType.NUMBER).description("판매가").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productStatusCode").type(JsonFieldType.STRING).description("상품상태").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.displayFlag").type(JsonFieldType.STRING).description("전시상태").attributes(key("etc").value("")),
                                        fieldWithPath("sellInfo.productStockQty").type(JsonFieldType.NUMBER).description("재고").attributes(key("etc").value("")),
                                        fieldWithPath("optionList[]").type(JsonFieldType.ARRAY).description("옵션 정보").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionSeq").type(JsonFieldType.NUMBER).description("옵션 번호").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionName").type(JsonFieldType.STRING).description("옵션 명").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionValue").type(JsonFieldType.STRING).description("옵션 항목").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionSurcharge").type(JsonFieldType.NUMBER).description("변동 금액").optional().attributes(key("etc").value("")),
                                        fieldWithPath("optionList[].optionStockQty").type(JsonFieldType.NUMBER).description("옵션 재고 수량").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery").type(JsonFieldType.OBJECT).description("배송 정보").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.deliveryType").type(JsonFieldType.STRING).description("배송방법").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.visitFlag").type(JsonFieldType.STRING).description("방문수령").attributes(key("etc").value("Y 설정, N 설정안함")),
                                        fieldWithPath("delivery.quickServiceFlag").type(JsonFieldType.STRING).description("퀵서비스").attributes(key("etc").value("Y 설정, N 설정안함")),
                                        fieldWithPath("delivery.deliveryBundleFlag").type(JsonFieldType.STRING).description("묶음배송").attributes(key("etc").value("Y 가능, N 불가")),
                                        fieldWithPath("delivery.chargeType").type(JsonFieldType.STRING).description("배송비 설정").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.chargeBase").type(JsonFieldType.NUMBER).description("기본 배송비").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.termsFreeCharge").type(JsonFieldType.NUMBER).description("(무료)배송비 기준 금액").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.paymentType").type(JsonFieldType.STRING).description("결제방식").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.surchargeFlag").type(JsonFieldType.STRING).description("제주/도서산간 배송비 추가 여부").attributes(key("etc").value("Y 설정, N 미설정")),
                                        fieldWithPath("delivery.areaCode").type(JsonFieldType.STRING).description("권역").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.surchargeJejudo").type(JsonFieldType.NUMBER).description("제주도 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.surchargeIsolated").type(JsonFieldType.NUMBER).description("도서산간 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringAddress").type(JsonFieldType.STRING).description("출고지").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnAddress").type(JsonFieldType.STRING).description("반품지").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnCharge").type(JsonFieldType.NUMBER).description("반품배송비(편도)").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnDeliveryCompanyCode").type(JsonFieldType.STRING).description("반품/교환 택배사 코드").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice").type(JsonFieldType.OBJECT).description("상품 정보 고시").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.categoryCode").type(JsonFieldType.STRING).description("상품 구분").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.modelName").type(JsonFieldType.STRING).description("품명/모델명").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productSize").type(JsonFieldType.STRING).description("크기").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productWeight").type(JsonFieldType.STRING).description("중량").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productMaterial").type(JsonFieldType.STRING).description("재질").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productComponent").type(JsonFieldType.STRING).description("제품구성").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.modelYear").type(JsonFieldType.STRING).description("출시연도").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.modelMonth").type(JsonFieldType.STRING).description("출시월").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.productMfr").type(JsonFieldType.STRING).description("제조자(사)").attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.detailSpec").type(JsonFieldType.STRING).description("세부사양").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.qaStandard").type(JsonFieldType.STRING).description("품질보증기준").optional().attributes(key("etc").value("")),
                                        fieldWithPath("infoNotice.asPhone").type(JsonFieldType.STRING).description("AS 연락처").attributes(key("etc").value("")),
                                        fieldWithPath("guide").type(JsonFieldType.OBJECT).description("안내 정보").attributes(key("etc").value("")),
                                        fieldWithPath("guide.productGuide").type(JsonFieldType.STRING).description("상품 안내 사항").attributes(key("etc").value("")),
                                        fieldWithPath("guide.deliveryGuide").type(JsonFieldType.STRING).description("배송 안내 사항").attributes(key("etc").value("")),
                                        fieldWithPath("guide.exchangeReturnGuide").type(JsonFieldType.STRING).description("교환/반품 안내 사항").attributes(key("etc").value("")),
                                        fieldWithPath("guide.asGuide").type(JsonFieldType.STRING).description("A/S 안내").attributes(key("etc").value("")),
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
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

}
