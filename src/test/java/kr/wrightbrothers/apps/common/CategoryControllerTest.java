package kr.wrightbrothers.apps.common;

import kr.wrightbrothers.BaseControllerTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

class CategoryControllerTest extends BaseControllerTests {

    @Test
    @DisplayName("카테고리 목록 조회")
    void findCategoryList() throws Exception {
        // 기본 카테고리 그룹 코드
        String categoryGroup = "000006";
        // 카테고리 목록 조회 API 테스트
        mockMvc.perform(get("/commons/categories")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                    .queryParam("categoryGroup", categoryGroup)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("commons-category-list",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("categoryGroup").description("카테고리 그룹").attributes(key("etc").value("(대)그룹코드 000006"))
                                ),
                                responseFields(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                                        fieldWithPath("data[].categoryCode").type(JsonFieldType.STRING).description("카테고리 코드"),
                                        fieldWithPath("data[].categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
        ;
    }

}
