package kr.wrightbrothers.apps;

import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.type.PaymentStatusCode;
import kr.wrightbrothers.apps.order.dto.OrderListDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

public class OrderControllerTest extends BaseControllerTests {

    @Test
    @DisplayName("주문상태 집계 건수")
    void findOrderStatusStatistics() throws Exception {
        // 조회 파라미터 필드
        OrderListDto.Param paramDto = OrderListDto.Param.builder()
                .orderStatus(
                        new String[]{
                                OrderStatusCode.COMPLETE_ORDER.getCode(),
                                OrderStatusCode.READY_PRODUCT.getCode(),
                                OrderStatusCode.CONFIRM_PURCHASE.getCode(),
                                OrderStatusCode.CANCEL_ORDER.getCode(),
                                OrderStatusCode.REQUEST_CANCEL.getCode(),
                                OrderStatusCode.COMPLETE_CANCEL.getCode(),
                                OrderStatusCode.START_DELIVERY.getCode(),
                                OrderStatusCode.PARTIAL_DELIVERY.getCode(),
                                OrderStatusCode.FINISH_DELIVERY.getCode()
                        }
                )
                .paymentStatus(
                        new String[]{
                                PaymentStatusCode.WAIT_DEPOSIT.getCode(),
                                PaymentStatusCode.COMPLETE_PAYMENT.getCode(),
                                PaymentStatusCode.CANCEL_PAYMENT.getCode(),
                                PaymentStatusCode.PARTIAL_CANCEL_PAYMENT.getCode()
                        }
                )
                .paymentMethod(
                        new String[]{
                                PaymentMethodCode.CARD.getCode(),
                                PaymentMethodCode.BANK.getCode(),
                                PaymentMethodCode.NON_BANK.getCode(),
                                PaymentMethodCode.PAYCO.getCode()
                        }
                )
                .rangeType("PAYMENT")
                .startDay("2022-01-01")
                .endDay("2022-10-10")
                .keywordType("NAME")
                .sortType("ORD")
                .build();

        // 주문내역 목록 조회 API 테스트
        mockMvc.perform(get("/v1/orders/status-statistics")
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.TEXT_HTML)
                        .queryParam("orderStatus", paramDto.getOrderStatus())
                        .queryParam("paymentStatus", paramDto.getPaymentStatus())
                        .queryParam("paymentMethod", paramDto.getPaymentMethod())
                        .queryParam("rangeType", paramDto.getRangeType())
                        .queryParam("startDay", paramDto.getStartDay())
                        .queryParam("endDay", paramDto.getEndDay())
                        .queryParam("keywordType", paramDto.getKeywordType())
                        .queryParam("keywordValue", paramDto.getKeywordValue())
                        .queryParam("sortType", paramDto.getSortType())
                        .queryParam("count", String.valueOf(1))
                        .queryParam("page", String.valueOf(1))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("order-status-statistics",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("orderStatus").description("주문 상태").attributes(key("etc").value("O05 주문완료, D01 상품 준비중, C05 구매확정, O06 취소요청, O07 취소완료, D02 배송중, D03 부분배송, D05 배송완료, O10 반품상태")),
                                        parameterWithName("paymentStatus").description("결제 상태").attributes(key("etc").value("S01 입금대기, S10 결제완료, S08 결제취소, S02 부분취소")),
                                        parameterWithName("paymentMethod").description("결제 수단").attributes(key("etc").value("P01 신용카드, P02 계좌이체, P03 무통장, P06 페이코")),
                                        parameterWithName("rangeType").description("조회 기간 구분").attributes(key("etc").value("PAYMENT 결제일자, CANCEL 취소일자")),
                                        parameterWithName("startDay").description("검색 시작 일자").attributes(key("etc").value("YYYY-MM-DD 예)2022-09-13")),
                                        parameterWithName("endDay").description("검색 종료 일자").attributes(key("etc").value("YYYY-MM-DD 예)2022-09-13")),
                                        parameterWithName("keywordType").description("키워드 구분").attributes(key("etc").value("NO 주문번호, NAME 주문명, USER 주문자")),
                                        parameterWithName("keywordValue").description("키워드 값").optional().attributes(key("etc").value("")),
                                        parameterWithName("sortType").description("정렬 구분").attributes(key("etc").value("ORD 주문일자순, PYT 결제일자순, HSAMT 판매가 높은순, LSAMT 판매가 낮은순")),
                                        parameterWithName("count").description("페이지 ROW 수").attributes(key("etc").value("")),
                                        parameterWithName("page").description("페이지").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("주문 상태 집계 정보"),
                                        fieldWithPath("data.totalOrderCount").type(JsonFieldType.NUMBER).description("전체 주문 건수"),
                                        fieldWithPath("data.completeOrderCount").type(JsonFieldType.NUMBER).description("주문 완료 건수"),
                                        fieldWithPath("data.readyProductCount").type(JsonFieldType.NUMBER).description("상품 준비 중 건수"),
                                        fieldWithPath("data.requestCancelCount").type(JsonFieldType.NUMBER).description("취소 요청 건수"),
                                        fieldWithPath("data.confirmPurchaseCount").type(JsonFieldType.NUMBER).description("구매 확정 건수"),
                                        fieldWithPath("data.completeReturnCount").type(JsonFieldType.NUMBER).description("반품 완료 건수"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                        ))
        ;
    }

    @Test
    @DisplayName("주문내역 목록 조회")
    void findOrderList() throws Exception {
        // 조회 파라미터 필드
        OrderListDto.Param paramDto = OrderListDto.Param.builder()
                .orderStatus(
                        new String[]{
                                OrderStatusCode.COMPLETE_ORDER.getCode(),
                                OrderStatusCode.READY_PRODUCT.getCode(),
                                OrderStatusCode.CONFIRM_PURCHASE.getCode(),
                                OrderStatusCode.CANCEL_ORDER.getCode(),
                                OrderStatusCode.REQUEST_CANCEL.getCode(),
                                OrderStatusCode.COMPLETE_CANCEL.getCode(),
                                OrderStatusCode.START_DELIVERY.getCode(),
                                OrderStatusCode.PARTIAL_DELIVERY.getCode(),
                                OrderStatusCode.FINISH_DELIVERY.getCode()
                        }
                )
                .paymentStatus(
                        new String[]{
                                PaymentStatusCode.WAIT_DEPOSIT.getCode(),
                                PaymentStatusCode.COMPLETE_PAYMENT.getCode(),
                                PaymentStatusCode.CANCEL_PAYMENT.getCode(),
                                PaymentStatusCode.PARTIAL_CANCEL_PAYMENT.getCode()
                        }
                )
                .paymentMethod(
                        new String[]{
                                PaymentMethodCode.CARD.getCode(),
                                PaymentMethodCode.BANK.getCode(),
                                PaymentMethodCode.NON_BANK.getCode(),
                                PaymentMethodCode.PAYCO.getCode()
                        }
                )
                .rangeType("PAYMENT")
                .startDay("2022-01-01")
                .endDay("2022-10-10")
                .keywordType("NAME")
                .sortType("ORD")
                .build();

        // 주문내역 목록 조회 API 테스트
        mockMvc.perform(get("/v1/orders")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                        .queryParam("orderStatus", paramDto.getOrderStatus())
                        .queryParam("paymentStatus", paramDto.getPaymentStatus())
                        .queryParam("paymentMethod", paramDto.getPaymentMethod())
                        .queryParam("rangeType", paramDto.getRangeType())
                        .queryParam("startDay", paramDto.getStartDay())
                        .queryParam("endDay", paramDto.getEndDay())
                        .queryParam("keywordType", paramDto.getKeywordType())
                        .queryParam("keywordValue", paramDto.getKeywordValue())
                        .queryParam("sortType", paramDto.getSortType())
                        .queryParam("count", String.valueOf(1))
                        .queryParam("page", String.valueOf(1))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("order-list",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                requestParameters(
                                        parameterWithName("orderStatus").description("주문 상태").attributes(key("etc").value("O05 주문완료, D01 상품 준비중, C05 구매확정, O06 취소요청, O07 취소완료, D02 배송중, D03 부분배송, D05 배송완료, O10 반품상태")),
                                        parameterWithName("paymentStatus").description("결제 상태").attributes(key("etc").value("S01 입금대기, S10 결제완료, S08 결제취소, S02 부분취소")),
                                        parameterWithName("paymentMethod").description("결제 수단").attributes(key("etc").value("P01 신용카드, P02 계좌이체, P03 무통장, P06 페이코")),
                                        parameterWithName("rangeType").description("조회 기간 구분").attributes(key("etc").value("PAYMENT 결제일자, CANCEL 취소일자")),
                                        parameterWithName("startDay").description("검색 시작 일자").attributes(key("etc").value("YYYY-MM-DD 예)2022-09-13")),
                                        parameterWithName("endDay").description("검색 종료 일자").attributes(key("etc").value("YYYY-MM-DD 예)2022-09-13")),
                                        parameterWithName("keywordType").description("키워드 구분").attributes(key("etc").value("NO 주문번호, NAME 주문명, USER 주문자")),
                                        parameterWithName("keywordValue").description("키워드 값").optional().attributes(key("etc").value("")),
                                        parameterWithName("sortType").description("정렬 구분").attributes(key("etc").value("ORD 주문일자순, PYT 결제일자순, HSAMT 판매가 높은순, LSAMT 판매가 낮은순")),
                                        parameterWithName("count").description("페이지 ROW 수").attributes(key("etc").value("")),
                                        parameterWithName("page").description("페이지").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).optional().description("주문 목록"),
                                        fieldWithPath("data[].orderDay").type(JsonFieldType.STRING).description("주문 일자"),
                                        fieldWithPath("data[].orderNo").type(JsonFieldType.STRING).description("주문 번호"),
                                        fieldWithPath("data[].orderUserName").type(JsonFieldType.STRING).description("주문자"),
                                        fieldWithPath("data[].orderStatusCode").type(JsonFieldType.STRING).description("주문 상태 코드"),
                                        fieldWithPath("data[].orderStatusName").type(JsonFieldType.STRING).description("주문 상태 명"),
                                        fieldWithPath("data[].paymentMethodCode").type(JsonFieldType.STRING).description("결제 수단 코드"),
                                        fieldWithPath("data[].paymentMethodName").type(JsonFieldType.STRING).description("결제 수단 명"),
                                        fieldWithPath("data[].orderName").type(JsonFieldType.STRING).description("주문 명"),
                                        fieldWithPath("data[].orderAmount").type(JsonFieldType.NUMBER).description("주문 금액"),
                                        fieldWithPath("data[].sspPoint").type(JsonFieldType.NUMBER).optional().description("S.S.P"),
                                        fieldWithPath("data[].salesAmount").type(JsonFieldType.NUMBER).optional().description("판매 대금"),
                                        fieldWithPath("data[].paymentAmount").type(JsonFieldType.NUMBER).optional().description("결제 금액"),
                                        fieldWithPath("data[].paymentDay").type(JsonFieldType.STRING).optional().description("결제 일자"),
                                        fieldWithPath("data[].cancelDay").type(JsonFieldType.STRING).optional().description("취소 일자"),
                                        fieldWithPath("data[].paymentStatusCode").type(JsonFieldType.STRING).description("결제 상태 코드"),
                                        fieldWithPath("data[].paymentStatusName").type(JsonFieldType.STRING).description("결제 상태 명"),
                                        fieldWithPath("totalItems").type(JsonFieldType.NUMBER).description("전체 조회 건수"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
            ;
    }

    @Test
    @DisplayName("주문내역 조회")
    void findOrder() throws Exception {
        String orderNo = "202209281757217262";

        // 주문내역 상세 API 조회
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/orders/{orderNo}", orderNo)
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.TEXT_HTML)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("order-find",
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
                                        fieldWithPath("data.order.orderStatusName").type(JsonFieldType.STRING).description("주문 상태"),
                                        fieldWithPath("data.order.orderQty").type(JsonFieldType.NUMBER).description("주문 수량"),
                                        fieldWithPath("data.order.orderUserCode").type(JsonFieldType.STRING).description("회원 코드"),
                                        fieldWithPath("data.order.orderUserId").type(JsonFieldType.STRING).description("아이디"),
                                        fieldWithPath("data.order.orderUserName").type(JsonFieldType.STRING).description("회원명"),
                                        fieldWithPath("data.order.orderUserPhone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data.order.recipientName").type(JsonFieldType.STRING).description("수령자 명"),
                                        fieldWithPath("data.order.recipientAddressZipCode").type(JsonFieldType.STRING).description("수령자 우편번호"),
                                        fieldWithPath("data.order.recipientAddress").type(JsonFieldType.STRING).description("수령자 주소"),
                                        fieldWithPath("data.order.recipientAddressDetail").type(JsonFieldType.STRING).description("수령자 상세주소"),
                                        fieldWithPath("data.order.requestDetail").type(JsonFieldType.STRING).optional().description("배송 요청 사항"),
                                        fieldWithPath("data.order.orderMemo").type(JsonFieldType.STRING).optional().description("주문 메모"),
                                        fieldWithPath("data.payment").type(JsonFieldType.OBJECT).description("결제 정보"),
                                        fieldWithPath("data.payment.orderAmount").type(JsonFieldType.NUMBER).description("주문 금액"),
                                        fieldWithPath("data.payment.deliveryChargeAmount").type(JsonFieldType.NUMBER).description("배송 금액"),
                                        fieldWithPath("data.payment.sspPoint").type(JsonFieldType.NUMBER).description("S.S.P"),
                                        fieldWithPath("data.payment.salesAmount").type(JsonFieldType.NUMBER).description("판매 대금"),
                                        fieldWithPath("data.payment.paymentAmount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                        fieldWithPath("data.payment.paymentDate").type(JsonFieldType.STRING).optional().description("결제 일시"),
                                        fieldWithPath("data.payment.transactionId").type(JsonFieldType.STRING).optional().description("PG 승인번호"),
                                        fieldWithPath("data.payment.paymentMethodName").type(JsonFieldType.STRING).description("결제 수단"),
                                        fieldWithPath("data.payment.paymentStatusName").type(JsonFieldType.STRING).description("결제 상태"),
                                        fieldWithPath("data.payment.cancelDate").type(JsonFieldType.STRING).description("취소 일시"),
                                        fieldWithPath("data.payment.cancelReason").type(JsonFieldType.STRING).description("취소 사유"),
                                        fieldWithPath("data.productList[]").type(JsonFieldType.ARRAY).description("주문 상품 목록"),
                                        fieldWithPath("data.productList[].orderProductSeq").type(JsonFieldType.NUMBER).description("주문 상품 SEQ"),
                                        fieldWithPath("data.productList[].productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data.productList[].productName").type(JsonFieldType.STRING).description("상품 명"),
                                        fieldWithPath("data.productList[].finalSellAmount").type(JsonFieldType.NUMBER).description("판매 금액"),
                                        fieldWithPath("data.productList[].optionName").type(JsonFieldType.STRING).description("옵션 명"),
                                        fieldWithPath("data.productList[].optionSurcharge").type(JsonFieldType.NUMBER).description("옵션 변동 금액"),
                                        fieldWithPath("data.productList[].deliveryType").type(JsonFieldType.STRING).optional().description("배송 구분"),
                                        fieldWithPath("data.productList[].deliveryCompanyName").type(JsonFieldType.STRING).optional().description("택배 업체"),
                                        fieldWithPath("data.productList[].invoiceNo").type(JsonFieldType.STRING).optional().description("송장 번호"),
                                        fieldWithPath("data.productList[].productQty").type(JsonFieldType.NUMBER).description("구매 수량"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

}
