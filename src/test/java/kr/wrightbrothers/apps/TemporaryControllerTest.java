package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.common.type.StorageType;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.apps.file.dto.FileUpdateDto;
import kr.wrightbrothers.apps.file.dto.FileUploadDto;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.file.service.S3Service;
import kr.wrightbrothers.apps.product.dto.*;
import kr.wrightbrothers.apps.temporary.dto.TemporaryDto;
import kr.wrightbrothers.apps.temporary.service.TemporaryService;
import kr.wrightbrothers.framework.util.JsonUtil;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TemporaryControllerTest extends BaseControllerTests {

    @Autowired
    private FileService fileService;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private TemporaryService temporaryService;

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
                        .productFileNo(fileUpload.getFileNo())
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
                        .productStatusCode(ProductStatusCode.SALE.getCode())
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
                        .modelName("Propel Advanced Disc 2")
                        .productSize("S")
                        .productWeight("9.2")
                        .productMaterial("재질")
                        .productComponent("재질구성")
                        .modelYear("2022")
                        .modelMonth("05")
                        .productMfr("(주)라이트브라더스")
                        .detailSpec("세부사양")
                        .qaStandard("품질보증기준")
                        .asPhone("02-000-0000")
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
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("임시저장")
    void mergeTemporary() throws Exception {
        TemporaryDto.ReqBody reqBody = TemporaryDto.ReqBody.builder()
                .storageType(StorageType.PRODUCT.getType())
                .storageData(JsonUtil.ToString(productDto))
                .build();

        // 임시저장 API 테스트
        mockMvc.perform(post("/v1/temporaries")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(reqBody))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("temporaries-post",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("storageType").type(JsonFieldType.STRING).description("임시저장 구분").attributes(key("etc").value("S01 상품")),
                                        fieldWithPath("storageData").type(JsonFieldType.STRING).description("임시저장 데이터").attributes(key("etc").value("JSON 형식의 문자 형태로 저장할 것"))
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
    @DisplayName("임시저장 조회")
    void findTemporary() throws Exception {
        // 임시저장 API 조회
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/temporaries/{storageType}", StorageType.PRODUCT.getType())
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("temporaries-find",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("storageType").description("임시저장 타입(S01 상품)")
                                ),
                                responseFields(
                                        fieldWithPath("data.storageData").type(JsonFieldType.STRING).description("임시저장 데이터(JSON 형식의 문자)").optional(),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("임시저장 삭제")
    void deleteTemporary() throws Exception {
        // 임시저장 API 삭제
        mockMvc.perform(delete("/v1/temporaries")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("storageType", StorageType.PRODUCT.getType())
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("temporaries-delete",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("storageType").description("임시저장 타입").attributes(key("etc").value("S01 상품"))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
                ;

        // 검증
        TemporaryDto.Response response = temporaryService.findTemporary(new TemporaryDto.Param(
                userDetailDto.getUserAuth().getPartnerCode(),
                userDetailDto.getUserId(),
                StorageType.PRODUCT.getType()
        ));
        assertNull(response);
    }

}
