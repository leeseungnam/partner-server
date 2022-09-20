package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.type.ProductLogCode;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.product.dto.StatusUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

public class ChangeInfoControllerTest extends ProductControllerTest {

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("상품 상태 변경 이력 조회")
    void findProductChangeHistory() throws Exception {

        // 이력 조회 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/products/{productCode}/change-history", productDto.getProduct().getProductCode())
                .header(AUTH_HEADER, JWT_TOKEN)
                .contentType(MediaType.TEXT_HTML)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andExpect(jsonPath("$.data.productCode").value(productDto.getProduct().getProductCode()))
                .andExpect(jsonPath("$.data.productName").value(productDto.getProduct().getProductName()))
                .andExpect(jsonPath("$.data.changeHistory[0].productLog").value("상품 등록"))
                .andExpect(jsonPath("$.data.changeHistory[0].productStatusCode").value(ProductStatusCode.PRODUCT_INSPECTION.getName()))
                .andExpect(jsonPath("$.data.changeHistory[0].productLogCode").value(ProductLogCode.REGISTER.getName()))
                .andDo(
                        document("change-history-find",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("productCode").description("상품 코드")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("상품 변경 이력 정보"),
                                        fieldWithPath("data.productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data.productName").type(JsonFieldType.STRING).description("상품 명"),
                                        fieldWithPath("data.changeHistory[]").type(JsonFieldType.ARRAY).description("변경 이력 목록 정보"),
                                        fieldWithPath("data.changeHistory[].productStatusCode").type(JsonFieldType.STRING).description("상품 진행 상태"),
                                        fieldWithPath("data.changeHistory[].productLogCode").type(JsonFieldType.STRING).description("상태"),
                                        fieldWithPath("data.changeHistory[].productLog").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.changeHistory[].createUserName").type(JsonFieldType.STRING).description("처리자"),
                                        fieldWithPath("data.changeHistory[].createDate").type(JsonFieldType.STRING).description("변경일"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )

                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("상품 상태 일괄 변경")
    void updateProductStatus() throws Exception {
        // 변경 요청 바디 생성
        StatusUpdateDto statusParam = StatusUpdateDto.builder()
                .productCodeList(new String[]{productDto.getProduct().getProductCode()})
                .changeType("C01")
                .changeValue("S02")
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
        ;
    }

}
