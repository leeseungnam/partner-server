package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.address.dto.AddressFindDto;
import kr.wrightbrothers.apps.address.dto.AddressInsertDto;
import kr.wrightbrothers.apps.address.dto.AddressUpdateDto;
import kr.wrightbrothers.apps.address.service.AddressService;
import kr.wrightbrothers.apps.common.util.PartnerKey;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

class AddressControllerTest extends BaseControllerTests {

    @Autowired
    private AddressService addressService;
    private AddressInsertDto paramDto;

    @BeforeEach
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    void setUpTest() {
        paramDto = AddressInsertDto.builder()
                .partnerCode("PT0000001")
                .addressName("자전거 출고지")
                .addressZipCode("06035")
                .address("서울특별시 강남구 강남대로 154길 37")
                .addressDetail("주경빌딩 2층")
                .addressPhone("0212345678")
                .repUnstoringFlag("N")
                .repReturnFlag("N")
                .userId("test@wrightbrothers.kr")
                .build();
        addressService.insertAddress(paramDto);
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    @DisplayName("주소록 목록 조회")
    void findAddressList() throws Exception {
        // 주소 목록 조회 API
        mockMvc.perform(get("/v1/addresses")
                .header(AUTH_HEADER, JWT_TOKEN)
                .contentType(MediaType.TEXT_HTML)
                        .queryParam("count", String.valueOf(1))
                        .queryParam("page", String.valueOf(1))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("address-list",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("count").description("페이지 ROW 수").attributes(key("etc").value("")),
                                        parameterWithName("page").description("페이지").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).optional().description("주소록 목록"),
                                        fieldWithPath("data[].addressNo").type(JsonFieldType.NUMBER).description("주소록 번호"),
                                        fieldWithPath("data[].addressName").type(JsonFieldType.STRING).description("주소록 이름"),
                                        fieldWithPath("data[].address").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("data[].addressPhone").type(JsonFieldType.STRING).description("연락처"),
                                        fieldWithPath("data[].reqUnstoringFlag").type(JsonFieldType.STRING).description("대표 출고지 주소로 지정 여부"),
                                        fieldWithPath("data[].reqReturnFlag").type(JsonFieldType.STRING).description("대표 반품/교환지 주소로 지정 여부"),
                                        fieldWithPath("totalItems").type(JsonFieldType.NUMBER).description("전체 조회 건수"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    @DisplayName("주소록 등록")
    void insertAddress() throws Exception {
        // 주소록 등록 객체
        AddressInsertDto paramDto = AddressInsertDto.builder()
                .addressName("자전거 출고지")
                .addressZipCode("06035")
                .address("서울특별시 강남구 강남대로 154길 37")
                .addressDetail("주경빌딩 2층")
                .addressPhone("0212345678")
                .repUnstoringFlag("Y")
                .repReturnFlag("N")
                .build();

        // 주소록 등록 API 테스트
        mockMvc.perform(post("/v1/addresses")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(paramDto))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("address-insert",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("addressName").type(JsonFieldType.STRING).description("주소록 이름").attributes(key("etc").value("")),
                                        fieldWithPath("addressZipCode").type(JsonFieldType.STRING).description("우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("address").type(JsonFieldType.STRING).description("주소").attributes(key("etc").value("")),
                                        fieldWithPath("addressDetail").type(JsonFieldType.STRING).description("상세주소").optional().attributes(key("etc").value("")),
                                        fieldWithPath("addressPhone").type(JsonFieldType.STRING).description("주소지 연락처").optional().attributes(key("etc").value("")),
                                        fieldWithPath("repUnstoringFlag").type(JsonFieldType.STRING).description("대표 출고지 주소로 지정 여부").attributes(key("etc").value("Y 지정, N 미지정")),
                                        fieldWithPath("repReturnFlag").type(JsonFieldType.STRING).description("대표 반품/교환지 주소로 지정 여부").attributes(key("etc").value("Y 지정, N 미지정"))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    @DisplayName("주소록 상세 조회")
    void findAddress() throws Exception {
        // 주소록 상세조회 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/addresses/{addressNo}", paramDto.getAddressNo())
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andExpect(jsonPath("$.data.addressName").value(paramDto.getAddressName()))
                .andExpect(jsonPath("$.data.addressZipCode").value(paramDto.getAddressZipCode()))
                .andExpect(jsonPath("$.data.address").value(paramDto.getAddress()))
                .andExpect(jsonPath("$.data.addressDetail").value(paramDto.getAddressDetail()))
                .andExpect(jsonPath("$.data.addressPhone").value(paramDto.getAddressPhone()))
                .andExpect(jsonPath("$.data.repUnstoringFlag").value(paramDto.getRepUnstoringFlag()))
                .andExpect(jsonPath("$.data.repReturnFlag").value(paramDto.getRepReturnFlag()))
                .andExpect(jsonPath("$.data.addressNo").value(paramDto.getAddressNo()))
                .andDo(
                        document("address-find",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("addressNo").description("주소록 번호")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("주소록 상세 정보"),
                                        fieldWithPath("data.addressNo").type(JsonFieldType.NUMBER).description("주소록 번호"),
                                        fieldWithPath("data.addressName").type(JsonFieldType.STRING).description("주소록 이름"),
                                        fieldWithPath("data.addressZipCode").type(JsonFieldType.STRING).description("우편번호"),
                                        fieldWithPath("data.address").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("data.addressDetail").type(JsonFieldType.STRING).description("상세주소"),
                                        fieldWithPath("data.addressPhone").type(JsonFieldType.STRING).description("주소지 연락처"),
                                        fieldWithPath("data.repUnstoringFlag").type(JsonFieldType.STRING).description("대표 출고지 주소로 지정 여부"),
                                        fieldWithPath("data.repReturnFlag").type(JsonFieldType.STRING).description("대표 반품/교환지 주소로 지정 여부"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;

    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    @DisplayName("주소록 수정")
    void updateAddress() throws Exception {
        // 주소록 수정 객체
        AddressUpdateDto updateDto = AddressUpdateDto.builder()
                .addressNo(paramDto.getAddressNo())
                .addressName("자전거 출고지 수정")
                .addressZipCode("06035")
                .address("수정 서울특별시 강남구 강남대로 154길 37")
                .addressDetail("수정 주경빌딩 1층")
                .addressPhone("1111111111")
                .repUnstoringFlag("Y")
                .repReturnFlag("Y")
                .build();

        // 주소록 수정 API 테스트
        mockMvc.perform(put("/v1/addresses")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(updateDto))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("address-update",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("addressNo").type(JsonFieldType.NUMBER).description("주소록 번호").attributes(key("etc").value("")),
                                        fieldWithPath("addressName").type(JsonFieldType.STRING).description("주소록 이름").attributes(key("etc").value("")),
                                        fieldWithPath("addressZipCode").type(JsonFieldType.STRING).description("우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("address").type(JsonFieldType.STRING).description("주소").attributes(key("etc").value("")),
                                        fieldWithPath("addressDetail").type(JsonFieldType.STRING).description("상세주소").optional().attributes(key("etc").value("")),
                                        fieldWithPath("addressPhone").type(JsonFieldType.STRING).description("주소지 연락처").optional().attributes(key("etc").value("")),
                                        fieldWithPath("repUnstoringFlag").type(JsonFieldType.STRING).description("대표 출고지 주소로 지정 여부").attributes(key("etc").value("Y 지정, N 미지정")),
                                        fieldWithPath("repReturnFlag").type(JsonFieldType.STRING).description("대표 반품/교환지 주소로 지정 여부").attributes(key("etc").value("Y 지정, N 미지정"))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;

        // 데이터 검증
        AddressFindDto.Response findDto = addressService.findAddress(
                AddressFindDto.Param.builder()
                        .partnerCode(paramDto.getPartnerCode())
                        .addressNo(updateDto.getAddressNo())
                        .build()
        );

        // 주소록 수정 검증
        assertEquals(updateDto.getAddressName(), findDto.getAddressName());
        assertEquals(updateDto.getAddressZipCode(), findDto.getAddressZipCode());
        assertEquals(updateDto.getAddress(), findDto.getAddress());
        assertEquals(updateDto.getAddressDetail(), findDto.getAddressDetail());
        assertEquals(updateDto.getAddressPhone(), findDto.getAddressPhone());
        assertEquals(updateDto.getRepUnstoringFlag(), findDto.getRepUnstoringFlag());
        assertEquals(updateDto.getRepReturnFlag(), findDto.getRepReturnFlag());
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    @DisplayName("주소록 삭제")
    void deleteAddress() throws Exception {
        // 템플릿 삭제 API 테스트
        mockMvc.perform(delete("/v1/addresses")
                .header(AUTH_HEADER, JWT_TOKEN)
                .contentType(MediaType.TEXT_HTML)
                        .queryParam("addressNo", String.valueOf(paramDto.getAddressNo()))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("address-delete",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("addressNo").description("주소록 번호").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))

        ;

    }

}
