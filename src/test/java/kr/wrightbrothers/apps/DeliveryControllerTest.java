package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.DeliveryStatusCode;
import kr.wrightbrothers.apps.common.type.DeliveryType;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.DeliveryInvoiceUpdateDto;
import kr.wrightbrothers.apps.order.dto.DeliveryListDto;
import kr.wrightbrothers.apps.order.dto.DeliveryMemoUpdateDto;
import kr.wrightbrothers.apps.order.dto.OrderFindDto;
import kr.wrightbrothers.apps.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeliveryControllerTest extends BaseControllerTests {

    @Autowired
    private OrderService orderService;

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

    @Test
    @DisplayName("배송내역 조회")
    void findDelivery() throws Exception {
        String orderNo = "202209281757217262";

        // 주문내역 상세 API 조회
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/deliveries/{orderNo}", orderNo)
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.TEXT_HTML)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("delivery-find",
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
                                        fieldWithPath("data.payment.paymentMethodName").type(JsonFieldType.STRING).description("결제 수단"),
                                        fieldWithPath("data.payment.paymentStatusName").type(JsonFieldType.STRING).description("결제 상태"),
                                        fieldWithPath("data.payment.cancelDate").type(JsonFieldType.STRING).description("취소 일시"),
                                        fieldWithPath("data.payment.cancelReason").type(JsonFieldType.STRING).description("취소 사유"),
                                        fieldWithPath("data.productList[]").type(JsonFieldType.ARRAY).description("주문 상품 목록"),
                                        fieldWithPath("data.productList[].orderProductSeq").type(JsonFieldType.NUMBER).description("주문 상품 SEQ"),
                                        fieldWithPath("data.productList[].productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data.productList[].productName").type(JsonFieldType.STRING).description("상품 명"),
                                        fieldWithPath("data.productList[].orderProductStatusName").type(JsonFieldType.STRING).description("주문 상품 상태"),
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
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                        ))
        ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("배송내역 수정")
    void updateDelivery() throws Exception {
        DeliveryMemoUpdateDto updateParam = DeliveryMemoUpdateDto.builder()
                .orderNo("202209281757217262")
                .recipientName("홍길동")
                .recipientPhone("01012341234")
                .recipientAddressZipCode("12345")
                .recipientAddress("서울특별시 강남구 강남대로 154길 37")
                .recipientAddressDetail("주경빌딩 2층")
                .deliveryMemo("배송메모")
                .build();

        // 배송내역 수정 API 테스트
        mockMvc.perform(put("/v1/deliveries")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content((new ObjectMapper().writeValueAsString(updateParam)))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("delivery-update",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문 번호").attributes(key("etc").value("")),
                                        fieldWithPath("recipientName").type(JsonFieldType.STRING).description("수령자 이름").attributes(key("etc").value("")),
                                        fieldWithPath("recipientPhone").type(JsonFieldType.STRING).description("수령자 휴대전화").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddressZipCode").type(JsonFieldType.STRING).description("배송지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddress").type(JsonFieldType.STRING).description("배송지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddressDetail").type(JsonFieldType.STRING).description("배송지 상세주소").attributes(key("etc").value("")),
                                        fieldWithPath("deliveryMemo").type(JsonFieldType.STRING).description("배송 메모").optional().attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
        ;

        // 변경 체크를 위한 조회
        OrderFindDto.Response nowDto = orderService.findOrder(OrderFindDto.Param.builder()
                .partnerCode("PT0000001")
                .orderNo("202209281757217262")
                .build());

        // 검증
        assertEquals(updateParam.getRecipientName(), nowDto.getOrder().getRecipientName());
        assertEquals(updateParam.getRecipientPhone(), nowDto.getOrder().getRecipientPhone());
        assertEquals(updateParam.getRecipientAddressZipCode(), nowDto.getOrder().getRecipientAddressZipCode());
        assertEquals(updateParam.getRecipientAddress(), nowDto.getOrder().getRecipientAddress());
        assertEquals(updateParam.getRecipientAddressDetail(), nowDto.getOrder().getRecipientAddressDetail());
        assertEquals(updateParam.getDeliveryMemo(), nowDto.getOrder().getDeliveryMemo());
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("송장번호 입력")
    void updateDeliveryInvoice() throws Exception {
        DeliveryInvoiceUpdateDto updateDto = DeliveryInvoiceUpdateDto.builder()
                .orderNo("202201011022429727")
                .orderProductSeq(new Integer[]{1})
                .deliveryCompanyCode("cjgls")
                .deliveryCompanyName("CJ대한통운")
                .invoiceNo("12345678890")
                .build();

        // 배송정보 수정 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/deliveries/{orderNo}/invoice", updateDto.getOrderNo())
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("delivery-invoice-update",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("orderNo").description("주문 번호")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문 번호").attributes(key("etc").value("")),
                                        fieldWithPath("orderProductSeq").type(JsonFieldType.ARRAY).description("주문 상품 SEQ").attributes(key("etc").value("")),
                                        fieldWithPath("deliveryCompanyCode").type(JsonFieldType.STRING).description("택배사 코드").attributes(key("etc").value("마스터 코드 000044")),
                                        fieldWithPath("deliveryCompanyName").type(JsonFieldType.STRING).description("택배사 이름").attributes(key("etc").value("")),
                                        fieldWithPath("invoiceNo").type(JsonFieldType.STRING).description("송장번호").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                        ))
        ;
    }

}