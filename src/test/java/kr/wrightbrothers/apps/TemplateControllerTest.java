package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.template.dto.*;
import kr.wrightbrothers.apps.template.service.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

class TemplateControllerTest extends BaseControllerTests {

    @Autowired
    private TemplateService templateService;
    private TemplateInsertDto paramDto;

    @BeforeEach
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    void setUpTest() {
        // 템플릿 등록 객체
        paramDto = TemplateInsertDto.builder()
                .partnerCode("PT0000001")
                .templateType("T01")
                .templateName("첫번째 배송 등록 테스트")
                .delivery(TemplateDeliveryDto.builder()
                        .deliveryType("D01")
                        .deliveryBundleFlag("N")
                        .chargeType("CT2")
                        .chargeBase(3000)
                        .termsFreeCharge(10000000L)
                        .paymentType("P02")
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
                .userId("test@wrightbrothers.kr")
                .build();
        templateService.insertTemplate(paramDto);
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("템플릿 목록 조회")
    void findTemplateList() throws Exception {
        // 조회 파라미터 필드
        TemplateListDto.Param paramDto = TemplateListDto.Param.builder()
                .templateType(new String[]{"T01", "T02", "T03", "T04"})
                .build();

        // 템플릿 목록 조회 API 테스트
        mockMvc.perform(get("/v1/templates")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                        .queryParam("templateType", paramDto.getTemplateType())
                        .queryParam("count", String.valueOf(1))
                        .queryParam("page", String.valueOf(1))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("template-list",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("templateType").description("템플릿 구분").attributes(key("etc").value("")),
                                        parameterWithName("count").description("페이지 ROW 수").attributes(key("etc").value("")),
                                        parameterWithName("page").description("페이지").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).optional().description("템플릿 목록"),
                                        fieldWithPath("data[].templateNo").type(JsonFieldType.NUMBER).description("템플릿 번호"),
                                        fieldWithPath("data[].templateType").type(JsonFieldType.STRING).description("템플릿 구분"),
                                        fieldWithPath("data[].templateName").type(JsonFieldType.STRING).description("템플릿 명"),
                                        fieldWithPath("data[].create_date").type(JsonFieldType.STRING).description("등록일시"),
                                        fieldWithPath("totalItems").type(JsonFieldType.NUMBER).description("전체 조회 건수"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("템플릿 등록")
    void insertTemplate() throws Exception {
        // 템플릿 등록 객체
        TemplateInsertDto paramDto = TemplateInsertDto.builder()
                .templateType("T01")
                .templateName("첫번째 배송 등록 테스트")
                .delivery(TemplateDeliveryDto.builder()
                        .deliveryType("D01")
                        .deliveryBundleFlag("N")
                        .chargeType("CT2")
                        .chargeBase(3000)
                        .termsFreeCharge(10000000L)
                        .paymentType("P02")
                        .surchargeFlag("N")
                        .unstoringZipCode("06035")
                        .unstoringAddress("서울특별시 강남구 강남대로 154길 37")
                        .unstoringAddressDetail("주경빌딩 1층")
                        .returnZipCode("06035")
                        .returnAddress("서울특별시 강남구 강남대로 154길 37")
                        .returnAddressDetail("주경빌딩 1층")
                        .exchangeCharge(100000)
                        .returnCharge(1000000)
                        .returnDeliveryCompanyCode("cjgls")
                        .build())
                .build();

        // 템플릿 등록 API 테스트
        mockMvc.perform(post("/v1/templates")
                .header(AUTH_HEADER, JWT_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(paramDto))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("template-insert",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("templateType").type(JsonFieldType.STRING).description("템플릿 구분").attributes(key("etc").value("T01 배송지, T02 A/S 안내, T03 배송 안내, T04 반품 안내")),
                                        fieldWithPath("templateName").type(JsonFieldType.STRING).description("템플릿 명").attributes(key("etc").value("")),
                                        fieldWithPath("templateGuide").type(JsonFieldType.STRING).description("템플릿 안내사항").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery").type(JsonFieldType.OBJECT).description("템플릿 배송 정보").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.deliveryType").type(JsonFieldType.STRING).description("배송방법").attributes(key("etc").value("1 택배/소포/등기, 3 직접배송(화물배달)")),
                                        fieldWithPath("delivery.deliveryBundleFlag").type(JsonFieldType.STRING).description("묶음배송").attributes(key("etc").value("Y 가능, N 불가")),
                                        fieldWithPath("delivery.chargeType").type(JsonFieldType.STRING).description("배송비 설정").attributes(key("etc").value("공통코드 000023")),
                                        fieldWithPath("delivery.chargeBase").type(JsonFieldType.NUMBER).description("기본 배송비").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.termsFreeCharge").type(JsonFieldType.NUMBER).description("(무료)배송비 기준 금액").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.paymentType").type(JsonFieldType.STRING).description("결제방식").attributes(key("etc").value("1 선결제, 2 착불, 3 착불 또는 선결제")),
                                        fieldWithPath("delivery.surchargeFlag").type(JsonFieldType.STRING).description("제주/도서산간 배송비 추가 여부").attributes(key("etc").value("Y 설정, N 미설정")),
                                        fieldWithPath("delivery.areaCode").type(JsonFieldType.STRING).description("권역").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.surchargeJejudo").type(JsonFieldType.NUMBER).description("제주도 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.surchargeIsolated").type(JsonFieldType.NUMBER).description("도서산간 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringZipCode").type(JsonFieldType.STRING).description("출고지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringAddress").type(JsonFieldType.STRING).description("출고지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringAddressDetail").type(JsonFieldType.STRING).description("출고지 상세주소").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnZipCode").type(JsonFieldType.STRING).description("반품지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnAddress").type(JsonFieldType.STRING).description("반품지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnAddressDetail").type(JsonFieldType.STRING).description("반품지 상세주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.exchangeCharge").type(JsonFieldType.NUMBER).description("교환배송비").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnCharge").type(JsonFieldType.NUMBER).description("반품배송비(편도)").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnDeliveryCompanyCode").type(JsonFieldType.STRING).description("반품/교환 택배사 코드").attributes(key("etc").value("공통코드 000044"))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
        ;

    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("템플릿 상세 조회")
    void findTemplate() throws Exception {
        templateService.insertTemplate(paramDto);

        // 템플릿 조회 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/templates/{templateNo}", paramDto.getTemplateNo())
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andExpect(jsonPath("$.data.templateType").value(paramDto.getTemplateType()))
                .andExpect(jsonPath("$.data.templateName").value(paramDto.getTemplateName()))
                .andExpect(jsonPath("$.data.templateGuide").value(paramDto.getTemplateGuide()))
                .andExpect(jsonPath("$.data.delivery.deliveryType").value(paramDto.getDelivery().getDeliveryType()))
                .andExpect(jsonPath("$.data.delivery.deliveryBundleFlag").value(paramDto.getDelivery().getDeliveryBundleFlag()))
                .andExpect(jsonPath("$.data.delivery.chargeType").value(paramDto.getDelivery().getChargeType()))
                .andExpect(jsonPath("$.data.delivery.chargeBase").value(paramDto.getDelivery().getChargeBase()))
                .andExpect(jsonPath("$.data.delivery.termsFreeCharge").value(paramDto.getDelivery().getTermsFreeCharge()))
                .andExpect(jsonPath("$.data.delivery.paymentType").value(paramDto.getDelivery().getPaymentType()))
                .andExpect(jsonPath("$.data.delivery.surchargeFlag").value(paramDto.getDelivery().getSurchargeFlag()))
                .andExpect(jsonPath("$.data.delivery.areaCode").value(paramDto.getDelivery().getAreaCode()))
                .andExpect(jsonPath("$.data.delivery.surchargeJejudo").value(paramDto.getDelivery().getSurchargeJejudo()))
                .andExpect(jsonPath("$.data.delivery.surchargeIsolated").value(paramDto.getDelivery().getSurchargeIsolated()))
                .andExpect(jsonPath("$.data.delivery.unstoringZipCode").value(paramDto.getDelivery().getUnstoringZipCode()))
                .andExpect(jsonPath("$.data.delivery.unstoringAddress").value(paramDto.getDelivery().getUnstoringAddress()))
                .andExpect(jsonPath("$.data.delivery.unstoringAddressDetail").value(paramDto.getDelivery().getUnstoringAddressDetail()))
                .andExpect(jsonPath("$.data.delivery.returnZipCode").value(paramDto.getDelivery().getReturnZipCode()))
                .andExpect(jsonPath("$.data.delivery.returnAddress").value(paramDto.getDelivery().getReturnAddress()))
                .andExpect(jsonPath("$.data.delivery.returnAddressDetail").value(paramDto.getDelivery().getReturnAddressDetail()))
                .andExpect(jsonPath("$.data.delivery.exchangeCharge").value(paramDto.getDelivery().getExchangeCharge()))
                .andExpect(jsonPath("$.data.delivery.returnCharge").value(paramDto.getDelivery().getReturnCharge()))
                .andExpect(jsonPath("$.data.delivery.returnDeliveryCompanyCode").value(paramDto.getDelivery().getReturnDeliveryCompanyCode()))
                .andDo(
                        document("template-find",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("templateNo").description("템플릿 번호")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("템플릿 상세 정보"),
                                        fieldWithPath("data.templateType").type(JsonFieldType.STRING).description("템플릿 구분"),
                                        fieldWithPath("data.templateName").type(JsonFieldType.STRING).description("템플릿 명"),
                                        fieldWithPath("data.templateGuide").type(JsonFieldType.STRING).description("템플릿 안내사항").optional(),
                                        fieldWithPath("data.delivery").type(JsonFieldType.OBJECT).description("템플릿 배송 정보").optional(),
                                        fieldWithPath("data.delivery.deliveryType").type(JsonFieldType.STRING).description("배송 방법"),
                                        fieldWithPath("data.delivery.deliveryBundleFlag").type(JsonFieldType.STRING).description("묶음배송 여부"),
                                        fieldWithPath("data.delivery.chargeType").type(JsonFieldType.STRING).description("배송비 구분"),
                                        fieldWithPath("data.delivery.chargeBase").type(JsonFieldType.NUMBER).description("기본 배송비"),
                                        fieldWithPath("data.delivery.termsFreeCharge").type(JsonFieldType.NUMBER).description("배송비 조건 금액"),
                                        fieldWithPath("data.delivery.paymentType").type(JsonFieldType.STRING).description("결제 방식"),
                                        fieldWithPath("data.delivery.surchargeFlag").type(JsonFieldType.STRING).description("제주/도서산간 추가 배송비 여부"),
                                        fieldWithPath("data.delivery.areaCode").type(JsonFieldType.STRING).description("권역").optional(),
                                        fieldWithPath("data.delivery.surchargeJejudo").type(JsonFieldType.STRING).description("제주 추가 배송비").optional(),
                                        fieldWithPath("data.delivery.surchargeIsolated").type(JsonFieldType.STRING).description("도서산간 추가 배송비").optional(),
                                        fieldWithPath("data.delivery.unstoringZipCode").type(JsonFieldType.STRING).description("출고지 우편번호"),
                                        fieldWithPath("data.delivery.unstoringAddress").type(JsonFieldType.STRING).description("출고지 주소"),
                                        fieldWithPath("data.delivery.unstoringAddressDetail").type(JsonFieldType.STRING).description("출고지 상세주소").optional(),
                                        fieldWithPath("data.delivery.returnZipCode").type(JsonFieldType.STRING).description("반품지 우편번호"),
                                        fieldWithPath("data.delivery.returnAddress").type(JsonFieldType.STRING).description("반품지 주소"),
                                        fieldWithPath("data.delivery.returnAddressDetail").type(JsonFieldType.STRING).description("반품지 상세주소"),
                                        fieldWithPath("data.delivery.exchangeCharge").type(JsonFieldType.NUMBER).description("교환배송비"),
                                        fieldWithPath("data.delivery.returnCharge").type(JsonFieldType.NUMBER).description("반품배송비(편도)"),
                                        fieldWithPath("data.delivery.returnDeliveryCompanyCode").type(JsonFieldType.STRING).description("반품/교환 택배사 코드"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("템플릿 수정")
    void updateTemplate() throws Exception {
        // 수정 객체
        TemplateUpdateDto updateDto = TemplateUpdateDto.builder()
                .templateNo(paramDto.getTemplateNo())
                .templateType("T01")
                .templateName("첫번째 배송 등록 테스트 수정")
                .delivery(TemplateDeliveryDto.builder()
                        .deliveryType("D01")
                        .deliveryBundleFlag("N")
                        .chargeType("CT2")
                        .chargeBase(3000)
                        .termsFreeCharge(10000000L)
                        .paymentType("P01")
                        .surchargeFlag("N")
                        .unstoringZipCode("11111")
                        .unstoringAddress("수정 서울특별시 강남구 강남대로 154길 37")
                        .unstoringAddressDetail("수정 주경빌딩 2층")
                        .returnZipCode("00000")
                        .returnAddress("수정 서울특별시 강남구 강남대로 154길 37")
                        .returnAddressDetail("수정 주경빌딩 1층")
                        .exchangeCharge(20000)
                        .returnCharge(9000000)
                        .returnDeliveryCompanyCode("cjgls")
                        .build())
                .build();

        mockMvc.perform(put("/v1/templates")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(updateDto))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("template-update",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("templateNo").type(JsonFieldType.NUMBER).description("템플릿 번호").attributes(key("etc").value("")),
                                        fieldWithPath("templateName").type(JsonFieldType.STRING).description("템플릿 명").attributes(key("etc").value("")),
                                        fieldWithPath("templateGuide").type(JsonFieldType.STRING).description("템플릿 안내사항").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery").type(JsonFieldType.OBJECT).description("템플릿 배송 정보").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.deliveryType").type(JsonFieldType.STRING).description("배송방법").attributes(key("etc").value("1 택배/소포/등기, 3 직접배송(화물배달)")),
                                        fieldWithPath("delivery.deliveryBundleFlag").type(JsonFieldType.STRING).description("묶음배송").attributes(key("etc").value("Y 가능, N 불가")),
                                        fieldWithPath("delivery.chargeType").type(JsonFieldType.STRING).description("배송비 설정").attributes(key("etc").value("공통코드 000023")),
                                        fieldWithPath("delivery.chargeBase").type(JsonFieldType.NUMBER).description("기본 배송비").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.termsFreeCharge").type(JsonFieldType.NUMBER).description("(무료)배송비 기준 금액").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.paymentType").type(JsonFieldType.STRING).description("결제방식").attributes(key("etc").value("1 선결제, 2 착불, 3 착불 또는 선결제")),
                                        fieldWithPath("delivery.surchargeFlag").type(JsonFieldType.STRING).description("제주/도서산간 배송비 추가 여부").attributes(key("etc").value("Y 설정, N 미설정")),
                                        fieldWithPath("delivery.areaCode").type(JsonFieldType.STRING).description("권역").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.surchargeJejudo").type(JsonFieldType.NUMBER).description("제주도 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.surchargeIsolated").type(JsonFieldType.NUMBER).description("도서산간 추가 배송비").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringZipCode").type(JsonFieldType.STRING).description("출고지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringAddress").type(JsonFieldType.STRING).description("출고지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.unstoringAddressDetail").type(JsonFieldType.STRING).description("출고지 상세주소").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnZipCode").type(JsonFieldType.STRING).description("반품지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnAddress").type(JsonFieldType.STRING).description("반품지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnAddressDetail").type(JsonFieldType.STRING).description("반품지 상세주소").optional().attributes(key("etc").value("")),
                                        fieldWithPath("delivery.exchangeCharge").type(JsonFieldType.NUMBER).description("교환배송비").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnCharge").type(JsonFieldType.NUMBER).description("반품배송비(편도)").attributes(key("etc").value("")),
                                        fieldWithPath("delivery.returnDeliveryCompanyCode").type(JsonFieldType.STRING).description("반품/교환 택배사 코드").attributes(key("etc").value("공통코드 000044"))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;

        // 데이터 검증
        TemplateFindDto.Response findDto = templateService.findTemplate(
                TemplateFindDto.Param.builder()
                        .partnerCode(paramDto.getPartnerCode())
                        .templateNo(updateDto.getTemplateNo())
                        .build());

        // 안내사항 수정 검증
        assertEquals(updateDto.getTemplateName(), findDto.getTemplateName());
        assertEquals(updateDto.getTemplateGuide(), findDto.getTemplateGuide());
        // 배송사항 수정 검증
        assertEquals(updateDto.getDelivery().getDeliveryType(), findDto.getDelivery().getDeliveryType());
        assertEquals(updateDto.getDelivery().getDeliveryBundleFlag(), findDto.getDelivery().getDeliveryBundleFlag());
        assertEquals(updateDto.getDelivery().getChargeType(), findDto.getDelivery().getChargeType());
        assertEquals(updateDto.getDelivery().getChargeBase(), findDto.getDelivery().getChargeBase());
        assertEquals(updateDto.getDelivery().getTermsFreeCharge(), findDto.getDelivery().getTermsFreeCharge());
        assertEquals(updateDto.getDelivery().getPaymentType(), findDto.getDelivery().getPaymentType());
        assertEquals(updateDto.getDelivery().getSurchargeFlag(), findDto.getDelivery().getSurchargeFlag());
        assertEquals(updateDto.getDelivery().getAreaCode(), findDto.getDelivery().getAreaCode());
        assertEquals(updateDto.getDelivery().getSurchargeIsolated(), findDto.getDelivery().getSurchargeIsolated());
        assertEquals(updateDto.getDelivery().getSurchargeJejudo(), findDto.getDelivery().getSurchargeJejudo());
        assertEquals(updateDto.getDelivery().getUnstoringZipCode(), findDto.getDelivery().getUnstoringZipCode());
        assertEquals(updateDto.getDelivery().getUnstoringAddress(), findDto.getDelivery().getUnstoringAddress());
        assertEquals(updateDto.getDelivery().getUnstoringAddressDetail(), findDto.getDelivery().getUnstoringAddressDetail());
        assertEquals(updateDto.getDelivery().getReturnZipCode(), findDto.getDelivery().getReturnZipCode());
        assertEquals(updateDto.getDelivery().getReturnAddress(), findDto.getDelivery().getReturnAddress());
        assertEquals(updateDto.getDelivery().getReturnAddressDetail(), findDto.getDelivery().getReturnAddressDetail());
        assertEquals(updateDto.getDelivery().getExchangeCharge(), findDto.getDelivery().getExchangeCharge());
        assertEquals(updateDto.getDelivery().getReturnCharge(), findDto.getDelivery().getReturnCharge());
        assertEquals(updateDto.getDelivery().getReturnDeliveryCompanyCode(), findDto.getDelivery().getReturnDeliveryCompanyCode());
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("템플릿 삭제")
    void deleteTemplate() throws Exception {
        // 템플릿 삭제 API 테스트
        mockMvc.perform(delete("/v1/templates")
                .header(AUTH_HEADER, JWT_TOKEN)
                .contentType(MediaType.TEXT_HTML)
                        .queryParam("templateNoList", new String[]{String.valueOf(paramDto.getTemplateNo())})
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("template-delete",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("templateNoList").description("템플릿 번호").attributes(key("etc").value("여러 건일경우 배열로 보낼 것"))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

}
