package kr.wrightbrothers.apps;

import kr.wrightbrothers.BaseControllerTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

public class FileControllerTest extends BaseControllerTests {

    @Test
    @DisplayName("파일 목록 조회")
    void findFileList() throws Exception {
        String fileNo = "202208080554004902761951652284278433";
        // 파일 목록 조회 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.get("/files/{fileNo}", fileNo)
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("file-list",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("fileNo").description("파일 대표 번호")
                                ),
                                responseFields(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("파일 목록"),
                                        fieldWithPath("data[].fileNo").type(JsonFieldType.STRING).description("파일 대표 번호"),
                                        fieldWithPath("data[].fileSeq").type(JsonFieldType.NUMBER).description("파일 순번"),
                                        fieldWithPath("data[].fileSource").type(JsonFieldType.STRING).description("파일 경로"),
                                        fieldWithPath("data[].fileSize").type(JsonFieldType.STRING).optional().description("파일 사이즈"),
                                        fieldWithPath("data[].fileOriginalName").type(JsonFieldType.STRING).description("원본 파일 이름"),
                                        fieldWithPath("data[].fileStatus").type(JsonFieldType.STRING).description("파일 상태"),
                                        fieldWithPath("data[].fileDescription").type(JsonFieldType.STRING).optional().description("파일 설명"),
                                        fieldWithPath("data[].displaySeq").type(JsonFieldType.NUMBER).description("디스플레이 순서"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @DisplayName("파일 이미지 업로드")
    void uploadImageFile() throws Exception {
        // 전송파일
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "<<png data>>".getBytes()
        );
        // 이미지 파일 업로드 API 테스트
        mockMvc.perform(multipart("/files/upload-image")
                    .file(file)
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("file-upload-image",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParts(
                                        partWithName("file").description("업로드 이미지")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("이미지 업로드 경로"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
        ;

    }

}
