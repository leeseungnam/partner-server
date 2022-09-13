package kr.wrightbrothers.apps;

import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.product.dto.ProductListDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

class ProductControllerTest extends BaseControllerTests {

    @Test
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

}
