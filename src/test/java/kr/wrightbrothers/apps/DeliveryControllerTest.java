package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.DeliveryStatusCode;
import kr.wrightbrothers.apps.common.type.DeliveryType;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeliveryControllerTest extends BaseControllerTests {

    @Autowired
    private OrderService orderService;

    @BeforeEach
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    void setUpTest() {
        dao.insert("kr.wrightbrothers.apps.order.query.Delivery.mockDeliveryData", null, PartnerKey.WBDataBase.Alias.Admin);
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
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
                .endDay("2022-11-31")
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
                                        parameterWithName("deliveryType").description("배송 방법").attributes(key("etc").value("D01 택배, D07 화물")),
                                        parameterWithName("deliveryStatus").description("배송 상태").attributes(key("etc").value("D01 상품준비중, D02 배송중, D05 배송완료, D03 부분배송")),
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
                                        fieldWithPath("data[].deliveryStatusCode").type(JsonFieldType.STRING).description("* 배송상태 코드"),
                                        fieldWithPath("data[].deliveryStatusName").type(JsonFieldType.STRING).description("* 배송상태 이름"),
                                        fieldWithPath("data[].paymentMethodCode").type(JsonFieldType.STRING).description("결제수단 코드"),
                                        fieldWithPath("data[].paymentMethodName").type(JsonFieldType.STRING).description("결제수단 이름"),
                                        fieldWithPath("data[].orderName").type(JsonFieldType.STRING).description("주문명"),
                                        fieldWithPath("data[].deliveryName").type(JsonFieldType.STRING).description("배송방법"),
                                        fieldWithPath("data[].recipientName").type(JsonFieldType.STRING).description("수령자"),
                                        fieldWithPath("data[].recipientPhone").type(JsonFieldType.STRING).description("휴대전화"),
                                        fieldWithPath("data[].recipientAddress").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("data[].recipientAddressDetail").type(JsonFieldType.STRING).description("상세주소"),
                                        fieldWithPath("data[].returnFlag").type(JsonFieldType.STRING).description("**** 반품여부"),
                                        fieldWithPath("totalItems").type(JsonFieldType.NUMBER).description("전체 조회 건수"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                        )
                )
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("배송내역 조회")
    void findDelivery() throws Exception {
        String orderNo = "192211151341424534";

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
                                        fieldWithPath("data.order.orderStatusCode").type(JsonFieldType.STRING).description("* 주문 상태 코드"),
                                        fieldWithPath("data.order.orderStatusName").type(JsonFieldType.STRING).description("주문 상태 이름"),
                                        fieldWithPath("data.order.orderQty").type(JsonFieldType.NUMBER).description("주문 수량"),
                                        fieldWithPath("data.order.orderUserCode").type(JsonFieldType.STRING).description("회원 코드"),
                                        fieldWithPath("data.order.orderUserId").type(JsonFieldType.STRING).description("아이디"),
                                        fieldWithPath("data.order.orderUserName").type(JsonFieldType.STRING).description("회원명"),
                                        fieldWithPath("data.order.orderUserPhone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data.order.recipientName").type(JsonFieldType.STRING).description("수령자 명"),
                                        fieldWithPath("data.order.recipientPhone").type(JsonFieldType.STRING).description("수령자 휴대전화"),
                                        fieldWithPath("data.order.recipientAddressZipCode").type(JsonFieldType.STRING).description("수령자 우편번호").optional(),
                                        fieldWithPath("data.order.recipientAddress").type(JsonFieldType.STRING).description("수령자 주소"),
                                        fieldWithPath("data.order.recipientAddressDetail").type(JsonFieldType.STRING).description("수령자 상세주소"),
                                        fieldWithPath("data.order.requestDetail").type(JsonFieldType.STRING).optional().description("배송 요청 사항"),
                                        fieldWithPath("data.order.orderMemo").type(JsonFieldType.STRING).optional().description("주문 메모"),
                                        fieldWithPath("data.order.returnMemo").type(JsonFieldType.STRING).optional().description("반품 메모"),
                                        fieldWithPath("data.order.deliveryMemo").type(JsonFieldType.STRING).optional().description("배송 메모"),
                                        fieldWithPath("data.payment").type(JsonFieldType.OBJECT).description("결제 정보"),
                                        fieldWithPath("data.payment.orderAmount").type(JsonFieldType.NUMBER).description("주문 금액"),
                                        fieldWithPath("data.payment.deliveryChargeAmount").type(JsonFieldType.NUMBER).description("배송 금액"),
                                        fieldWithPath("data.payment.paymentAmount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                        fieldWithPath("data.payment.paymentDate").type(JsonFieldType.STRING).optional().description("결제 일시"),
                                        fieldWithPath("data.payment.approvalNo").type(JsonFieldType.STRING).optional().description("* PG 승인번호"),
                                        fieldWithPath("data.payment.rentalAmount").type(JsonFieldType.NUMBER).optional().description("* 월 렌탈료"),
                                        fieldWithPath("data.payment.paymentMethodCode").type(JsonFieldType.STRING).description("결제 수단 코드"),
                                        fieldWithPath("data.payment.paymentMethodName").type(JsonFieldType.STRING).description("결제 수단 이름"),
                                        fieldWithPath("data.payment.paymentStatusCode").type(JsonFieldType.STRING).description("결제 상태 코드"),
                                        fieldWithPath("data.payment.paymentStatusName").type(JsonFieldType.STRING).description("결제 상태 이름"),
                                        fieldWithPath("data.deliveryList[]").type(JsonFieldType.ARRAY).optional().description("배송진행 상품 목록"),
                                        fieldWithPath("data.deliveryList[].orderProductSeq").type(JsonFieldType.NUMBER).description("주문 상품 SEQ"),
                                        fieldWithPath("data.deliveryList[].productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data.deliveryList[].productName").type(JsonFieldType.STRING).description("상품 이름"),
                                        fieldWithPath("data.deliveryList[].optionName").type(JsonFieldType.STRING).description("옵션 이름"),
                                        fieldWithPath("data.deliveryList[].productQty").type(JsonFieldType.NUMBER).description("상품 수량"),
                                        fieldWithPath("data.deliveryList[].deliveryCompanyCode").type(JsonFieldType.STRING).optional().description("택배사 코드"),
                                        fieldWithPath("data.deliveryList[].deliveryCompanyName").type(JsonFieldType.STRING).optional().description("택배사 이름"),
                                        fieldWithPath("data.deliveryList[].invoiceNo").type(JsonFieldType.STRING).optional().description("송장번호"),
                                        fieldWithPath("data.deliveryList[].recipientName").type(JsonFieldType.STRING).optional().description("* 수령자 명"),
                                        fieldWithPath("data.deliveryList[].recipientPhone").type(JsonFieldType.STRING).optional().description("* 수령자 휴대전화"),
                                        fieldWithPath("data.deliveryList[].recipientAddress").type(JsonFieldType.STRING).optional().description("* 수령자 주소"),
                                        fieldWithPath("data.deliveryList[].deliveryStartDay").type(JsonFieldType.STRING).optional().description("배송 시작일"),
                                        fieldWithPath("data.deliveryList[].deliveryEndDay").type(JsonFieldType.STRING).optional().description("배송 완료일"),
                                        fieldWithPath("data.deliveryList[].deliveryStatusCode").type(JsonFieldType.STRING).optional().description("택배 진행 상태 코드"),
                                        fieldWithPath("data.deliveryList[].deliveryStatusName").type(JsonFieldType.STRING).optional().description("택배 진행 상태 이름"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                        ))
        ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("배송메모 수정")
    void updateDeliveryMemo() throws Exception {
        DeliveryMemoUpdateDto updateParam = DeliveryMemoUpdateDto.builder()
                .orderNo("192211151341424534")
                .deliveryMemo("배송메모 변경변경")
                .build();

        // 배송내역 수정 API 테스트
        mockMvc.perform(patch("/v1/deliveries")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content((new ObjectMapper().writeValueAsString(updateParam)))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("delivery-memo-update",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문 번호").attributes(key("etc").value("")),
                                        fieldWithPath("deliveryMemo").type(JsonFieldType.STRING).description("배송 메모").optional().attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
        ;

        // 변경 체크를 위한 조회
        OrderFindDto.Response nowDto = orderService.findOrder(OrderFindDto.Param.builder()
                .partnerCode("PT0000001")
                .orderNo("192211151341424534")
                .build());

        // 검증
        assertEquals(updateParam.getDeliveryMemo(), nowDto.getOrder().getDeliveryMemo());
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("배송지 저장")
    void updateDelivery() throws Exception {
        DeliveryUpdateDto updateDto = DeliveryUpdateDto.builder()
                .orderNo("192211151341424534")
                .orderProductSeqArray(new Integer[]{1})
                .recipientName("수령자명")
                .recipientPhone("01047183922")
                .recipientAddressZipCode("12345")
                .recipientAddress("서울시 그 어디인가에 주소")
                .recipientAddressDetail("블루하우스")
                .build();

        // 배송정보 수정 API 테스트
        mockMvc.perform(put("/v1/deliveries")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content((new ObjectMapper().writeValueAsString(updateDto)))
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
                                        fieldWithPath("orderProductSeqArray").type(JsonFieldType.ARRAY).description("주문 상품 SEQ").attributes(key("etc").value("")),
                                        fieldWithPath("recipientName").type(JsonFieldType.STRING).description("수령자명").attributes(key("etc").value("마스터 코드 000044")),
                                        fieldWithPath("recipientPhone").type(JsonFieldType.STRING).description("수령자 연락처").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddressZipCode").type(JsonFieldType.STRING).description("수령자 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddress").type(JsonFieldType.STRING).description("수령자 주소").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddressDetail").type(JsonFieldType.STRING).description("수령자 상세주소").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("송장번호 저장")
    void updateDeliveryInvoice() throws Exception {
        DeliveryInvoiceUpdateDto updateDto = DeliveryInvoiceUpdateDto.builder()
                .orderNo("192211151341424534")
                .orderProductSeqArray(new Integer[]{1})
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
                                        fieldWithPath("orderProductSeqArray").type(JsonFieldType.ARRAY).description("주문 상품 SEQ").attributes(key("etc").value("")),
                                        fieldWithPath("deliveryCompanyCode").type(JsonFieldType.STRING).description("택배사 코드").attributes(key("etc").value("마스터 코드 000044")),
                                        fieldWithPath("deliveryCompanyName").type(JsonFieldType.STRING).description("택배사 이름").attributes(key("etc").value("")),
                                        fieldWithPath("invoiceNo").type(JsonFieldType.STRING).description("송장번호").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                        ))
        ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("화물배송 완료")
    void udpateDeliveryFreight() throws Exception {
        DeliveryFreightUpdateDto updateDto = DeliveryFreightUpdateDto.builder()
                .orderNo("192211151341424534")
                .orderProductSeqArray(new Integer[]{1})
                .build();

        // 화물배송 완료 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/deliveries/{orderNo}/freights", updateDto.getOrderNo())
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(updateDto))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("delivery-freight-update",
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
                                        fieldWithPath("orderProductSeqArray").type(JsonFieldType.ARRAY).description("주문 상품 SEQ").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("배송지 정보 조회")
    void findDeliveryAddresses() throws Exception {
        String orderNo = "192211151341424534";

        // 주문내역 상세 API 조회
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/deliveries/{orderNo}/addresses/{orderProductSeq}", orderNo, 1)
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.TEXT_HTML)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("delivery-addresses-find",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("orderNo").description("주문 번호"),
                                        parameterWithName("orderProductSeq").description("주문상품 SEQ")
                                ),
                                responseFields(
                                        fieldWithPath("data.recipientName").type(JsonFieldType.STRING).description("수령자명"),
                                        fieldWithPath("data.recipientPhone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data.recipientAddressZipCode").type(JsonFieldType.STRING).description("우편번호"),
                                        fieldWithPath("data.recipientAddress").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("data.recipientAddressDetail").type(JsonFieldType.STRING).description("상세주소"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                        ))
        ;
    }



}