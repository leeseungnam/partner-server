package kr.wrightbrothers.apps;

import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.apps.file.dto.FileUpdateDto;
import kr.wrightbrothers.apps.file.dto.FileUploadDto;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.file.service.S3Service;
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
import java.io.FileInputStream;
import java.io.IOException;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

public class FileControllerTest extends BaseControllerTests {

    @Autowired
    private FileService fileService;
    @Autowired
    private S3Service s3Service;
    private String fileNo;

    @BeforeEach
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Admin)
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
        dao.update("kr.wrightbrothers.apps.file.query.File.updateFile",
                FileUpdateDto.builder()
                        .fileNo(fileUpload.getFileNo())
                        .fileSeq(fileUpload.getFileSeq())
                        .fileSource(fileUpload.getFileSource())
                        .displaySeq(1)
                        .userId("test@wrightbrothers.kr")
                        .build(), PartnerKey.WBDataBase.Alias.Admin);

        fileNo = fileUpload.getFileNo();
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Admin)
    @DisplayName("파일 목록 조회")
    void findFileList() throws Exception {
        // 파일 목록 조회 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/files/{fileNo}", fileNo)
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
                                        fieldWithPath("data[].fileStatus").type(JsonFieldType.STRING).description("T-임시, I-저장, D-삭제, R-읽기"),
                                        fieldWithPath("data[].fileDescription").type(JsonFieldType.STRING).optional().description("파일 설명"),
                                        fieldWithPath("data[].displaySeq").type(JsonFieldType.NUMBER).description("디스플레이 순서"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }
    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Admin)
    @DisplayName("파일 업로드")
    void uploadFile() throws Exception {
        // 업로드 파일
        MockMultipartFile files = new MockMultipartFile(
                "files",
                "image.png",
                "image/png",
                "<<png data>>".getBytes()
        );
        // 파일 업로드 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/v1/files/upload/{fileNo}", "0")
                    .file(files)
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("file-upload",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParts(
                                        partWithName("files").description("업로드 파일")
                                ),
                                pathParameters(
                                        parameterWithName("fileNo").description("파일 대표 번호")
                                ),
                                responseFields(
                                        fieldWithPath("fileDataSet[]").type(JsonFieldType.ARRAY).description("업로드 파일 목록"),
                                        fieldWithPath("fileDataSet[].fileNo").type(JsonFieldType.STRING).description("파일 대표 번호"),
                                        fieldWithPath("fileDataSet[].fileSeq").type(JsonFieldType.NUMBER).description("파일 번호"),
                                        fieldWithPath("fileDataSet[].fileSource").type(JsonFieldType.STRING).description("파일 경로"),
                                        fieldWithPath("fileDataSet[].fileSize").type(JsonFieldType.STRING).description("파일 사이즈"),
                                        fieldWithPath("fileDataSet[].fileOriginalName").type(JsonFieldType.STRING).description("원본 파일 이름"),
                                        fieldWithPath("fileDataSet[].fileStatus").type(JsonFieldType.STRING).description("T-임시, I-저장, D-삭제, R-읽기"),
                                        fieldWithPath("fileDataSet[].fileDescription").type(JsonFieldType.STRING).optional().description("파일 설명"),
                                        fieldWithPath("fileDataSet[].displaySeq").type(JsonFieldType.NUMBER).description("디스플레이 순서"),
                                        fieldWithPath("fileDataSet[].tifPath").type(JsonFieldType.STRING).optional().description("TIF 경로"),
                                        fieldWithPath("fileNo").type(JsonFieldType.STRING).description("파일 대표 번호"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
                ;

    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Admin)
    @DisplayName("파일 업로드(TIF 전용)")
    void uploadTifFile() throws Exception {
        // 업로드 파일
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.tif",
                "image/tif",
                new FileInputStream("src/test/resources/upload/test.jpeg")
        );
        // TIF 파일 업로드 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/v1/files/upload-tif/{fileNo}/{productCode}", "0", "0000000000")
                    .file(file)
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("file-upload-tif",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParts(
                                        partWithName("file").description("업로드 이미지")
                                ),
                                pathParameters(
                                        parameterWithName("fileNo").description("파일 대표 번호"),
                                        parameterWithName("productCode").description("상품 코드")
                                ),
                                responseFields(
                                        fieldWithPath("fileDataSet").type(JsonFieldType.OBJECT).description("업로드 TIF 파일 정보"),
                                        fieldWithPath("fileDataSet.fileNo").type(JsonFieldType.STRING).description("파일 대표 번호"),
                                        fieldWithPath("fileDataSet.fileSeq").type(JsonFieldType.NUMBER).description("파일 번호"),
                                        fieldWithPath("fileDataSet.fileSource").type(JsonFieldType.STRING).description("파일 경로"),
                                        fieldWithPath("fileDataSet.fileSize").type(JsonFieldType.STRING).description("파일 사이즈"),
                                        fieldWithPath("fileDataSet.fileOriginalName").type(JsonFieldType.STRING).description("원본 파일 이름"),
                                        fieldWithPath("fileDataSet.fileStatus").type(JsonFieldType.STRING).description("T-임시, I-저장, D-삭제, R-읽기"),
                                        fieldWithPath("fileDataSet.fileDescription").type(JsonFieldType.STRING).optional().description("파일 설명"),
                                        fieldWithPath("fileDataSet.displaySeq").type(JsonFieldType.NUMBER).description("디스플레이 순서"),
                                        fieldWithPath("fileDataSet.tifPath").type(JsonFieldType.STRING).description("TIF 경로"),
                                        fieldWithPath("fileNo").type(JsonFieldType.STRING).description("파일 대표 번호"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Admin)
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
        mockMvc.perform(multipart("/v1/files/upload-image")
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
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
        ;

    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Admin)
    @DisplayName("파일 다운로드")
    void downloadFile() throws Exception {
        // 파일 다운로드 API 테스트
        mockMvc.perform(get("/v1/files/download")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                        .queryParam("fileNo", fileNo)
                        .queryParam("fileSeq", String.valueOf(1))
                    .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("file-download",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("fileNo").description("파일 대표 번호").attributes(key("etc").value("")),
                                        parameterWithName("fileSeq").description("파일 번호").attributes(key("etc").value(""))
                                )
                ))
        ;
    }

}
