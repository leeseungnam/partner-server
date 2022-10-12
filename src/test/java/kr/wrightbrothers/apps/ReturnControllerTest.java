package kr.wrightbrothers.apps;

import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import kr.wrightbrothers.apps.order.dto.RequestReturnUpdateDto;
import kr.wrightbrothers.apps.order.dto.ReturnListDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReturnControllerTest extends BaseControllerTests {

    @Test
    @DisplayName("반품 목록 조회")
    void findReturnList() throws Exception {
        // 조회 파라미터 필드
        ReturnListDto.Param paramDto = ReturnListDto.Param.builder()
                .returnStatus(
                        new String[]{
                                OrderProductStatusCode.REQUEST_RETURN.getCode(),
                                OrderProductStatusCode.WITHDRAWAL_RETURN.getCode(),
                                OrderProductStatusCode.START_RETURN.getCode(),
                                OrderProductStatusCode.COMPLETE_RETURN.getCode(),
                                OrderProductStatusCode.NON_RETURN.getCode()
                        }
                )
                .rangeType("PAYMENT")
                .startDay("2022-01-01")
                .endDay("2022-10-10")
                .keywordType("NO")
                .keywordValue("")
                .build();

        // 반품 관리 목록 조회 API 테스트
        mockMvc.perform(get("/v1/returns")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                        .queryParam("returnStatus", paramDto.getReturnStatus())
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
                        document("return-list",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("returnStatus").description("반품 상태").attributes(key("etc").value("")),
                                        parameterWithName("rangeType").description("기간 구분").attributes(key("etc").value("")),
                                        parameterWithName("startDay").description("검색 시작 일자").attributes(key("etc").value("YYYY-MM-DD 예)2022-09-13")),
                                        parameterWithName("endDay").description("검색 종료 일자").attributes(key("etc").value("YYYY-MM-DD 예)2022-09-13")),
                                        parameterWithName("keywordType").description("키워드 구분").attributes(key("etc").value("NO 주문번호, NAME 주문명, USER 주문자")),
                                        parameterWithName("keywordValue").description("키워드 값").optional().attributes(key("etc").value("")),
                                        parameterWithName("count").description("페이지 ROW 수").attributes(key("etc").value("")),
                                        parameterWithName("page").description("페이지").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).optional().description("반품 목록 정보"),
                                        fieldWithPath("data[].returnRequestDay").type(JsonFieldType.STRING).optional().description("반품요청"),
                                        fieldWithPath("data[].orderNo").type(JsonFieldType.STRING).description("주문번호"),
                                        fieldWithPath("data[].orderDay").type(JsonFieldType.STRING).description("주문일자"),
                                        fieldWithPath("data[].orderUserName").type(JsonFieldType.STRING).description("주문자"),
                                        fieldWithPath("data[].orderProductStatusCode").type(JsonFieldType.STRING).description("반품상태 코드"),
                                        fieldWithPath("data[].orderProductStatusName").type(JsonFieldType.STRING).description("반품상태 이름"),
                                        fieldWithPath("data[].orderStatusCode").type(JsonFieldType.STRING).description("주문상태 코드"),
                                        fieldWithPath("data[].orderStatusName").type(JsonFieldType.STRING).description("주문상태 이름"),
                                        fieldWithPath("data[].paymentMethodCode").type(JsonFieldType.STRING).description("결제수단 코드"),
                                        fieldWithPath("data[].paymentMethodName").type(JsonFieldType.STRING).description("결제수단 이름"),
                                        fieldWithPath("data[].orderName").type(JsonFieldType.STRING).description("주문명"),
                                        fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("반품 요청 상품"),
                                        fieldWithPath("data[].returnReason").type(JsonFieldType.STRING).optional().description("반품 사유"),
                                        fieldWithPath("data[].orderAmount").type(JsonFieldType.NUMBER).description("주문 금액"),
                                        fieldWithPath("data[].finalSellAmount").type(JsonFieldType.NUMBER).description("반품 금액"),
                                        fieldWithPath("data[].sspPoint").type(JsonFieldType.NUMBER).description("S.S.P"),
                                        fieldWithPath("data[].salesAmount").type(JsonFieldType.NUMBER).description("판매대금"),
                                        fieldWithPath("data[].cancelDay").type(JsonFieldType.STRING).description("취소일자"),
                                        fieldWithPath("data[].paymentStatusCode").type(JsonFieldType.STRING).description("결제상태 코드"),
                                        fieldWithPath("data[].paymentStatusName").type(JsonFieldType.STRING).description("결제상태 이름"),
                                        fieldWithPath("totalItems").type(JsonFieldType.NUMBER).description("전체 조회 건수"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @DisplayName("반품내역 조회")
    void findReturn() throws Exception {
        String orderNo = "202209281757217262";

        // 반품내역 상세 API 조회
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/returns/{orderNo}", orderNo)
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.TEXT_HTML)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("return-find",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("orderNo").description("주문 번호")
                                ),
                                responseFields(
                                        fieldWithPath("data.order").type(JsonFieldType.OBJECT).description("주문 정보"),
                                        fieldWithPath("data.order.orderNo").type(JsonFieldType.STRING).description("주문 번호"),
                                        fieldWithPath("data.order.orderDate").type(JsonFieldType.STRING).description("주문 일시"),
                                        fieldWithPath("data.order.orderStatusCode").type(JsonFieldType.STRING).description("* 주문 상태 코드"),
                                        fieldWithPath("data.order.orderStatusName").type(JsonFieldType.STRING).description("주문 상태 이름"),
                                        fieldWithPath("data.order.orderQty").type(JsonFieldType.NUMBER).description("주문 수량"),
                                        fieldWithPath("data.order.orderUserCode").type(JsonFieldType.STRING).description("회원 코드"),
                                        fieldWithPath("data.order.orderUserId").type(JsonFieldType.STRING).description("아이디"),
                                        fieldWithPath("data.order.orderUserName").type(JsonFieldType.STRING).description("회원명"),
                                        fieldWithPath("data.order.orderUserPhone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data.order.recipientName").type(JsonFieldType.STRING).description("수령자 명"),
                                        fieldWithPath("data.order.recipientPhone").type(JsonFieldType.STRING).description("수령자 휴대전화"),
                                        fieldWithPath("data.order.recipientAddressZipCode").type(JsonFieldType.STRING).description("수령자 우편번호"),
                                        fieldWithPath("data.order.recipientAddress").type(JsonFieldType.STRING).description("수령자 주소"),
                                        fieldWithPath("data.order.recipientAddressDetail").type(JsonFieldType.STRING).description("수령자 상세주소"),
                                        fieldWithPath("data.order.requestDetail").type(JsonFieldType.STRING).optional().description("배송 요청 사항"),
                                        fieldWithPath("data.order.orderMemo").type(JsonFieldType.STRING).optional().description("주문 메모"),
                                        fieldWithPath("data.order.returnMemo").type(JsonFieldType.STRING).optional().description("반품 메모"),
                                        fieldWithPath("data.order.deliveryMemo").type(JsonFieldType.STRING).optional().description("배송 메모"),
                                        fieldWithPath("data.payment").type(JsonFieldType.OBJECT).description("결제 정보"),
                                        fieldWithPath("data.payment.orderAmount").type(JsonFieldType.NUMBER).description("주문 금액"),
                                        fieldWithPath("data.payment.deliveryChargeAmount").type(JsonFieldType.NUMBER).description("배송 금액"),
                                        fieldWithPath("data.payment.sspPoint").type(JsonFieldType.NUMBER).description("S.S.P"),
                                        fieldWithPath("data.payment.salesAmount").type(JsonFieldType.NUMBER).description("판매 대금"),
                                        fieldWithPath("data.payment.paymentAmount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                        fieldWithPath("data.payment.paymentDate").type(JsonFieldType.STRING).optional().description("결제 일시"),
                                        fieldWithPath("data.payment.transactionId").type(JsonFieldType.STRING).optional().description("PG 승인번호"),
                                        fieldWithPath("data.payment.paymentMethodCode").type(JsonFieldType.STRING).description("* 결제 수단 코드"),
                                        fieldWithPath("data.payment.paymentMethodName").type(JsonFieldType.STRING).description("결제 수단 이름"),
                                        fieldWithPath("data.payment.paymentStatusCode").type(JsonFieldType.STRING).description("* 결제 상태 코드"),
                                        fieldWithPath("data.payment.paymentStatusName").type(JsonFieldType.STRING).description("결제 상태 이름"),
                                        fieldWithPath("data.payment.cancelDate").type(JsonFieldType.STRING).description("취소 일시"),
                                        fieldWithPath("data.payment.cancelReason").type(JsonFieldType.STRING).description("취소 사유"),
                                        fieldWithPath("data.productList[]").type(JsonFieldType.ARRAY).description("주문 상품 목록"),
                                        fieldWithPath("data.productList[].orderProductSeq").type(JsonFieldType.NUMBER).description("주문 상품 SEQ"),
                                        fieldWithPath("data.productList[].productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data.productList[].productName").type(JsonFieldType.STRING).description("상품 명"),
                                        fieldWithPath("data.productList[].orderProductStatusCode").type(JsonFieldType.STRING).description("주문 상품 상태 코드"),
                                        fieldWithPath("data.productList[].orderProductStatusName").type(JsonFieldType.STRING).description("주문 상품 상태 이름"),
                                        fieldWithPath("data.productList[].finalSellAmount").type(JsonFieldType.NUMBER).description("판매 금액"),
                                        fieldWithPath("data.productList[].optionName").type(JsonFieldType.STRING).description("옵션 명"),
                                        fieldWithPath("data.productList[].optionSurcharge").type(JsonFieldType.NUMBER).description("옵션 변동 금액"),
                                        fieldWithPath("data.productList[].productQty").type(JsonFieldType.NUMBER).description("구매 수량"),
                                        fieldWithPath("data.productList[].deliveryType").type(JsonFieldType.STRING).optional().description("배송 구분"),
                                        fieldWithPath("data.productList[].deliveryCompanyName").type(JsonFieldType.STRING).optional().description("택배 업체"),
                                        fieldWithPath("data.productList[].deliveryChargeAmount").type(JsonFieldType.NUMBER).optional().description("배송료"),
                                        fieldWithPath("data.productList[].invoiceNo").type(JsonFieldType.STRING).optional().description("송장 번호"),
                                        fieldWithPath("data.productList[].returnDeliveryCompanyName").type(JsonFieldType.STRING).optional().description("반품 택배 업체"),
                                        fieldWithPath("data.productList[].returnInvoiceNo").type(JsonFieldType.STRING).optional().description("반품 송장 번"),
                                        fieldWithPath("data.productList[].returnReason").type(JsonFieldType.STRING).optional().description("반품 사유"),
                                        fieldWithPath("data.returnProductList[]").type(JsonFieldType.ARRAY).optional().description("반품 요청 상품 목록"),
                                        fieldWithPath("data.returnProductList[].orderProductSeq").type(JsonFieldType.NUMBER).description("주문 상품 SEQ"),
                                        fieldWithPath("data.returnProductList[].productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data.returnProductList[].productName").type(JsonFieldType.STRING).description("상품 이름"),
                                        fieldWithPath("data.returnProductList[].optionName").type(JsonFieldType.STRING).description("옵션 이름"),
                                        fieldWithPath("data.returnProductList[].finalSellAmount").type(JsonFieldType.NUMBER).description("판매 금액"),
                                        fieldWithPath("data.returnProductList[].returnRequestDay").type(JsonFieldType.STRING).optional().description("반품 요청 일자"),
                                        fieldWithPath("data.returnProductList[].productQty").type(JsonFieldType.NUMBER).description("반품 요청 수량"),
                                        fieldWithPath("data.returnProductList[].orderProductStatusCode").type(JsonFieldType.STRING).description("상품 진행 상태 코드"),
                                        fieldWithPath("data.returnProductList[].orderProductStatusName").type(JsonFieldType.STRING).description("상품 진행 상태 이름"),
                                        fieldWithPath("data.returnProductList[].returnDeliveryCompanyName").type(JsonFieldType.STRING).optional().description("반품 택배사 이름"),
                                        fieldWithPath("data.returnProductList[].returnInvoiceNo").type(JsonFieldType.STRING).optional().description("반품 송장 번호"),
                                        fieldWithPath("data.returnProductList[].reason").type(JsonFieldType.STRING).optional().description("사유"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

}
