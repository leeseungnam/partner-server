package kr.wrightbrothers.apps;

import kr.wrightbrothers.BaseControllerTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

class CodeControllerTest extends BaseControllerTests {

    @Test
    @DisplayName("코드 목록 조회")
    void findCodeList() throws Exception {

        String codeGroup = "000057";
        // 코드 목록 조회 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/master-code/{codeGroup}/codes", codeGroup)
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("code-list",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("codeGroup").description("코드 그룹")
                                ),
                                responseFields(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("코드 목록"),
                                        fieldWithPath("data[].codeValue").type(JsonFieldType.STRING).description("코드 값"),
                                        fieldWithPath("data[].codeName").type(JsonFieldType.STRING).description("코드 이름"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
        ;

    }

}