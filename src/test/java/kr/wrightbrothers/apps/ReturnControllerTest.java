package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReturnControllerTest extends BaseControllerTests {

    @BeforeEach
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    void setUpTest() {
        dao.insert("kr.wrightbrothers.apps.order.query.Return.mockReturnData", null, PartnerKey.WBDataBase.Alias.Admin);
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("반품 목록 조회")
    void findReturnList() throws Exception {
        // 조회 파라미터 필드
        ReturnListDto.Param paramDto = ReturnListDto.Param.builder()
                .returnStatus(
                        new String[]{
                                OrderConst.ProductStatus.REQUEST_RETURN.getCode(),
                                OrderConst.ProductStatus.START_RETURN.getCode()
                        }
                )
                .rangeType("PAYMENT")
                .startDay("2020-01-01")
                .endDay("2022-11-17")
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
                                        parameterWithName("returnStatus").description("반품 상태").attributes(key("etc").value("R01 반품요청, R03 반품진행, R04 반품불가, R05 반품완료, R06 반품완료요청")),
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
                                        fieldWithPath("data[].returnStatusCode").type(JsonFieldType.STRING).description("반품상태 코드"),
                                        fieldWithPath("data[].returnStatusName").type(JsonFieldType.STRING).description("반품상태 이름"),
                                        fieldWithPath("data[].paymentMethodCode").type(JsonFieldType.STRING).description("결제수단 코드"),
                                        fieldWithPath("data[].paymentMethodName").type(JsonFieldType.STRING).description("결제수단 이름"),
                                        fieldWithPath("data[].orderName").type(JsonFieldType.STRING).description("주문명"),
                                        fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("반품 요청 상품"),
                                        fieldWithPath("data[].returnReason").type(JsonFieldType.STRING).optional().description("반품 사유"),
                                        fieldWithPath("data[].orderAmount").type(JsonFieldType.NUMBER).description("주문 금액"),
                                        fieldWithPath("data[].finalSellAmount").type(JsonFieldType.NUMBER).description("반품 금액"),
                                        fieldWithPath("data[].returnDeliveryChargeAmount").type(JsonFieldType.NUMBER).optional().description("반품 배송 금액"),
                                        fieldWithPath("totalItems").type(JsonFieldType.NUMBER).description("전체 조회 건수"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("반품내역 조회")
    void findReturn() throws Exception {
        String orderNo = "192211151341424534";

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
                                        fieldWithPath("data.order.orderStatusCode").type(JsonFieldType.STRING).description("주문 상태 코드"),
                                        fieldWithPath("data.order.orderStatusName").type(JsonFieldType.STRING).description("주문 상태 이름"),
                                        fieldWithPath("data.order.orderQty").type(JsonFieldType.NUMBER).description("주문 수량"),
                                        fieldWithPath("data.order.orderUserCode").type(JsonFieldType.STRING).description("회원 코드").optional(),
                                        fieldWithPath("data.order.orderUserId").type(JsonFieldType.STRING).description("아이디").optional(),
                                        fieldWithPath("data.order.orderUserName").type(JsonFieldType.STRING).description("회원명").optional(),
                                        fieldWithPath("data.order.orderUserPhone").type(JsonFieldType.STRING).description("휴대폰 번호").optional(),
                                        fieldWithPath("data.order.recipientName").type(JsonFieldType.STRING).description("반품자 명").optional(),
                                        fieldWithPath("data.order.recipientPhone").type(JsonFieldType.STRING).description("반품자 휴대전화").optional(),
                                        fieldWithPath("data.order.recipientAddressZipCode").type(JsonFieldType.STRING).description("반품자 우편번호").optional(),
                                        fieldWithPath("data.order.recipientAddress").type(JsonFieldType.STRING).description("반품자 주소").optional(),
                                        fieldWithPath("data.order.recipientAddressDetail").type(JsonFieldType.STRING).description("반품자 상세주소").optional(),
                                        fieldWithPath("data.order.requestDetail").type(JsonFieldType.STRING).optional().description("배송 요청 사항"),
                                        fieldWithPath("data.order.orderMemo").type(JsonFieldType.STRING).optional().description("주문 메모"),
                                        fieldWithPath("data.order.returnMemo").type(JsonFieldType.STRING).optional().description("반품 메모"),
                                        fieldWithPath("data.order.deliveryMemo").type(JsonFieldType.STRING).optional().description("배송 메모"),
                                        fieldWithPath("data.payment").type(JsonFieldType.OBJECT).description("결제 정보"),
                                        fieldWithPath("data.payment.orderAmount").type(JsonFieldType.NUMBER).description("주문 금액"),
                                        fieldWithPath("data.payment.deliveryChargeAmount").type(JsonFieldType.NUMBER).description("배송 금액"),
                                        fieldWithPath("data.payment.sspPoint").type(JsonFieldType.NUMBER).description("배송 금액"),
                                        fieldWithPath("data.payment.saleAmount").type(JsonFieldType.NUMBER).description("배송 금액"),
                                        fieldWithPath("data.payment.paymentAmount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                        fieldWithPath("data.payment.paymentDate").type(JsonFieldType.STRING).optional().description("결제 일시"),
                                        fieldWithPath("data.payment.approvalNo").type(JsonFieldType.STRING).optional().description("PG 승인번호"),
                                        fieldWithPath("data.payment.rentalAmount").type(JsonFieldType.NUMBER).optional().description("월 렌탈료"),
                                        fieldWithPath("data.payment.paymentMethodCode").type(JsonFieldType.STRING).description("결제 수단 코드"),
                                        fieldWithPath("data.payment.paymentMethodName").type(JsonFieldType.STRING).description("결제 수단 이름"),
                                        fieldWithPath("data.payment.paymentStatusCode").type(JsonFieldType.STRING).description("결제 상태 코드"),
                                        fieldWithPath("data.payment.paymentStatusName").type(JsonFieldType.STRING).description("결제 상태 이름"),
                                        fieldWithPath("data.returnProductList[]").type(JsonFieldType.ARRAY).optional().description("반품 요청 상품 목록"),
                                        fieldWithPath("data.returnProductList[].orderProductSeq").type(JsonFieldType.NUMBER).description("주문 상품 SEQ"),
                                        fieldWithPath("data.returnProductList[].productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data.returnProductList[].productName").type(JsonFieldType.STRING).description("상품 이름"),
                                        fieldWithPath("data.returnProductList[].optionName").type(JsonFieldType.STRING).description("옵션 이름"),
                                        fieldWithPath("data.returnProductList[].finalSellAmount").type(JsonFieldType.NUMBER).description("판매 금액"),
                                        fieldWithPath("data.returnProductList[].returnRequestDay").type(JsonFieldType.STRING).optional().description("반품 요청 일자"),
                                        fieldWithPath("data.returnProductList[].returnCompleteDay").type(JsonFieldType.STRING).optional().description("반품 완료 일자"),
                                        fieldWithPath("data.returnProductList[].productQty").type(JsonFieldType.NUMBER).description("반품 요청 수량"),
                                        fieldWithPath("data.returnProductList[].orderProductStatusCode").type(JsonFieldType.STRING).description("상품 진행 상태 코드"),
                                        fieldWithPath("data.returnProductList[].orderProductStatusName").type(JsonFieldType.STRING).description("상품 진행 상태 이름"),
                                        fieldWithPath("data.returnProductList[].returnDeliveryCompanyCode").type(JsonFieldType.STRING).optional().description("반품 택배사 코드"),
                                        fieldWithPath("data.returnProductList[].returnDeliveryCompanyName").type(JsonFieldType.STRING).optional().description("반품 택배사 이름"),
                                        fieldWithPath("data.returnProductList[].returnInvoiceNo").type(JsonFieldType.STRING).optional().description("반품 송장 번호"),
                                        fieldWithPath("data.returnProductList[].returnReason").type(JsonFieldType.STRING).optional().description("반품 사유"),
                                        fieldWithPath("data.returnProductList[].nonReturnReason").type(JsonFieldType.STRING).optional().description("반품 불가 사유"),
                                        fieldWithPath("data.returnProductList[].returnDeliveryAmount").type(JsonFieldType.NUMBER).optional().description("(추우가)반품배송금액"),
                                        fieldWithPath("data.returnProductList[].refundAmount").type(JsonFieldType.NUMBER).optional().description("(추우가)결제취소금액"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("반품메모 수정")
    void updateReturn() throws Exception {
        ReturnMemoUpdateDto updateParam = ReturnMemoUpdateDto.builder()
                .orderNo("192211151341424534")
                .returnMemo("반품메모")
                .build();

        // 반품관리 수정 API 테스트
        mockMvc.perform(patch("/v1/returns")
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content((new ObjectMapper().writeValueAsString(updateParam)))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("return-update",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문 번호").attributes(key("etc").value("")),
                                        fieldWithPath("returnMemo").type(JsonFieldType.STRING).description("반품 메모").optional().attributes(key("etc").value(""))
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
    @DisplayName("반품 승인 처리")
    void updateApprovalReturn() throws Exception {
        ApprovalReturnDto updateParam = ApprovalReturnDto.builder()
                .orderNo("192211151341424534")
                .orderProductSeqArray(new Integer[]{1})
                .deliveryCompanyCode("cjsnf")
                .invoiceNo("123123123")
                .build();

        // 반품 요청 상품 처리 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/returns/{orderNo}/approval-return", updateParam.getOrderNo())
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(updateParam))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("approval-return-update",
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
                                        fieldWithPath("deliveryCompanyCode").type(JsonFieldType.STRING).description("택배사 코드").optional().attributes(key("etc").value("마스터코드 000044")),
                                        fieldWithPath("invoiceNo").type(JsonFieldType.STRING).description("송장번호").optional().attributes(key("etc").value(""))
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
    @DisplayName("반품 취소 처리")
    void updateCancelReturn() throws Exception {
        CancelReturnDto updateParam = CancelReturnDto.builder()
                .orderNo("192211151341424534")
                .orderProductSeqArray(new Integer[]{1})
                .build();

        // 반품 요청 상품 처리 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/returns/{orderNo}/cancel-return", updateParam.getOrderNo())
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateParam))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("cancel-return-update",
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
    @DisplayName("반품 완료 처리")
    void updateCompleteReturn() throws Exception {
        RequestReturnDto updateParam = RequestReturnDto.builder()
                .orderNo("192211151341424534")
                .orderProductSeqArray(new Integer[]{1})
                .paymentAmount(100L)
                .refundAmount(100L)
                .returnDeliveryAmount(0L)
                .build();

        // 반품 요청 상품 처리 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/returns/{orderNo}/complete-return", updateParam.getOrderNo())
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateParam))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("complete-return-update",
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
                                        fieldWithPath("returnDeliveryAmount").type(JsonFieldType.NUMBER).description("(추우가)반품 배송비").attributes(key("etc").value("")),
                                        fieldWithPath("paymentAmount").type(JsonFieldType.NUMBER).description("(추우가)결제 금액").attributes(key("etc").value("")),
                                        fieldWithPath("refundAmount").type(JsonFieldType.NUMBER).description("(추우가)환불 예정 금액").attributes(key("etc").value(""))
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
    @DisplayName("반품 불가 처리")
    void updateNonReturn() throws Exception {
        NonReturnDto updateParam = NonReturnDto.builder()
                .orderNo("192211151341424534")
                .orderProductSeqArray(new Integer[]{1})
                .reasonCode("R03")
                .build();

        // 반품 요청 상품 처리 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/returns/{orderNo}/non-return", updateParam.getOrderNo())
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateParam))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("non-return-update",
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
                                        fieldWithPath("reasonCode").type(JsonFieldType.STRING).description("불가 사유").attributes(key("etc").value("마스터코드 000085"))
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
    @DisplayName("반품 배송정보 조회")
    void findReturnDelivery() throws Exception {
        String orderNo = "192211151341424534";

        // 반품 배송정보 조회 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/returns/{orderNo}/deliveries/{orderProductSeq}", orderNo, 1)
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("return-delivery-find",
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
                                        fieldWithPath("data.deliveryCompanyCode").type(JsonFieldType.STRING).description("택배사 코드"),
                                        fieldWithPath("data.invoiceNo").type(JsonFieldType.STRING).description("송장번호"),
                                        fieldWithPath("data.recipientName").type(JsonFieldType.STRING).description("수령자 이름"),
                                        fieldWithPath("data.recipientPhone").type(JsonFieldType.STRING).description("수령자 핸드폰"),
                                        fieldWithPath("data.recipientAddressZipCode").type(JsonFieldType.STRING).description("수령자 우편번"),
                                        fieldWithPath("data.recipientAddress").type(JsonFieldType.STRING).description("수령자 주소"),
                                        fieldWithPath("data.recipientAddressDetail").type(JsonFieldType.STRING).description("수령자 상세주소"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("반품 배송정보 수정")
    void updateReturnDelivery() throws Exception {

        ReturnDeliveryDto.ReqBody updateParam = ReturnDeliveryDto.ReqBody.builder()
                .orderNo("192211151341424534")
                .orderProductSeq(1)
                .deliveryCompanyCode("ckdhjf")
                .invoiceNo("304958584")
                .recipientName("수령자")
                .recipientPhone("01011111111")
                .recipientAddressZipCode("12345")
                .recipientAddress("대한민국 그 어딘가...")
                .recipientAddressDetail("라이트브라더스 1층")
                .build();

        // 반품 배송정보 수정 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/returns/{orderNo}/deliveries", updateParam.getOrderNo())
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateParam))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("return-delivery-update",
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
                                        fieldWithPath("orderProductSeq").type(JsonFieldType.NUMBER).description("주문 상품 SEQ").attributes(key("etc").value("")),
                                        fieldWithPath("deliveryCompanyCode").type(JsonFieldType.STRING).description("택배사").attributes(key("etc").value("마스터코드 000044")),
                                        fieldWithPath("invoiceNo").type(JsonFieldType.STRING).description("송장번호").attributes(key("etc").value("")),
                                        fieldWithPath("recipientName").type(JsonFieldType.STRING).description("수령자").attributes(key("etc").value("")),
                                        fieldWithPath("recipientPhone").type(JsonFieldType.STRING).description("연락처").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddressZipCode").type(JsonFieldType.STRING).description("우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddress").type(JsonFieldType.STRING).description("주소").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddressDetail").type(JsonFieldType.STRING).description("상세주소").optional().attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                        ))
        ;

    }
}
