package kr.wrightbrothers.apps;

import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.DeliveryStatusCode;
import kr.wrightbrothers.apps.common.type.DeliveryType;
import kr.wrightbrothers.apps.order.dto.DeliveryListDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeliveryControllerTest extends BaseControllerTests {

    @Test
    @DisplayName("배송내역 목록 조회")
    void findDeliveryList() throws Exception {
        // 조회 파라미터 필드
        DeliveryListDto.Param paramDto = DeliveryListDto.Param.builder()
                .deliveryType(
                        new String[]{
                                DeliveryType.PARCEL.getType(),
                                DeliveryType.FREIGHT.getType()
                        }
                )
                .deliveryStatus(
                        new String[]{
                                DeliveryStatusCode.READY_PRODUCT.getCode(),
                                DeliveryStatusCode.START_DELIVERY.getCode(),
                                DeliveryStatusCode.FINISH_DELIVERY.getCode(),
                                DeliveryStatusCode.PARTIAL_DELIVERY.getCode()
                        }
                )
                .startDay("2022-01-01")
                .endDay("2022-10-10")
                .keywordType("NAME")
                .keywordValue("")
                .build();

        // 배송관리 목록 조회 API 테스트
        mockMvc.perform(get("/v1/deliveries")
                .header(AUTH_HEADER, JWT_TOKEN)
                .contentType(MediaType.TEXT_HTML)
                    .queryParam("deliveryType", paramDto.getDeliveryType())
                    .queryParam("deliveryStatus", paramDto.getDeliveryStatus())
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
                        document("delivery-list",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("deliveryType").description("배송 방법").attributes(key("etc").value("")),
                                        parameterWithName("deliveryStatus").description("배송 상태").attributes(key("etc").value("")),
                                        parameterWithName("startDay").description("검색 시작 일자").attributes(key("etc").value("YYYY-MM-DD 예)2022-09-13")),
                                        parameterWithName("endDay").description("검색 종료 일자").attributes(key("etc").value("YYYY-MM-DD 예)2022-09-13")),
                                        parameterWithName("keywordType").description("키워드 구분").attributes(key("etc").value("NO 주문번호, NAME 주문명, USER 주문자")),
                                        parameterWithName("keywordValue").description("키워드 값").optional().attributes(key("etc").value("")),
                                        parameterWithName("count").description("페이지 ROW 수").attributes(key("etc").value("")),
                                        parameterWithName("page").description("페이지").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).optional().description("배송 목록 정보"),
                                        fieldWithPath("data[].paymentDay").type(JsonFieldType.STRING).description("결제일자"),
                                        fieldWithPath("data[].orderNo").type(JsonFieldType.STRING).description("주문번호"),
                                        fieldWithPath("data[].orderUserName").type(JsonFieldType.STRING).description("주문자"),
                                        fieldWithPath("data[].orderStatusCode").type(JsonFieldType.STRING).description("주문상태 코드"),
                                        fieldWithPath("data[].orderStatusName").type(JsonFieldType.STRING).description("주문상태 이름"),
                                        fieldWithPath("data[].paymentMethodCode").type(JsonFieldType.STRING).description("결제수단 코드"),
                                        fieldWithPath("data[].paymentMethodName").type(JsonFieldType.STRING).description("결제수단 이름"),
                                        fieldWithPath("data[].orderName").type(JsonFieldType.STRING).description("주문명"),
                                        fieldWithPath("data[].deliveryName").type(JsonFieldType.STRING).description("배송방법"),
                                        fieldWithPath("data[].recipientName").type(JsonFieldType.STRING).description("수령자"),
                                        fieldWithPath("data[].recipientPhone").type(JsonFieldType.STRING).description("휴대전화"),
                                        fieldWithPath("data[].recipientAddress").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("data[].recipientAddressDetail").type(JsonFieldType.STRING).description("상세주소"),
                                        fieldWithPath("totalItems").type(JsonFieldType.NUMBER).description("전체 조회 건수"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                        )
                )
                ;
    }

}