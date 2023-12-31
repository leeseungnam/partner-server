package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.constants.PaymentConst;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.OrderFindDto;
import kr.wrightbrothers.apps.order.dto.PaymentCancelDto;
import kr.wrightbrothers.apps.order.dto.PaymentRefundDto;
import kr.wrightbrothers.apps.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

public class PaymentControllerTest extends BaseControllerTests {

    @Autowired
    private OrderService orderService;

    @BeforeEach
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    void setUpTest() {
        dao.insert("kr.wrightbrothers.apps.order.query.Order.mockOrderData", null, PartnerKey.WBDataBase.Alias.Admin);
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("주문내역 상품 결제취소 요청")
    void updatePaymentCancel() throws Exception {
        PaymentCancelDto cancelDto = PaymentCancelDto.builder()
                .orderNo("192211151341424534")
                .orderProductSeq(new Integer[]{1})
                .cancelReasonCode("C01")
                .cancelReasonName("구매 의사 취소")
                .paymentMethodCode(PaymentConst.Method.CARD.getCode())
                .build();

        // 주문내역 결제취소 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/payments/{orderNo}/cancel", cancelDto.getOrderNo())
                        .header(AUTH_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cancelDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("payment-cancel-update",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문 번호").attributes(key("etc").value("")),
                                        fieldWithPath("orderProductSeq").type(JsonFieldType.ARRAY).description("주문 상품 SEQ").attributes(key("etc").value("")),
                                        fieldWithPath("cancelReasonCode").type(JsonFieldType.STRING).description("취소 사유 코드").attributes(key("etc").value("마스터 코드 000074")),
                                        fieldWithPath("cancelReasonName").type(JsonFieldType.STRING).description("취소 사유 이름").attributes(key("etc").value("")),
                                        fieldWithPath("paymentMethodCode").type(JsonFieldType.STRING).description("결제 수단 코드").attributes(key("etc").value("")),
                                        fieldWithPath("refundBankCode").type(JsonFieldType.STRING).description("환불 은행 코드").optional().attributes(key("etc").value("마스터 코드 000046, 무통장 거래시 처리")),
                                        fieldWithPath("refundBankName").type(JsonFieldType.STRING).description("환불 은행 이름").optional().attributes(key("etc").value("무통장 거래시 처리")),
                                        fieldWithPath("refundBankAccountNo").type(JsonFieldType.STRING).description("환불 은행 계좌번호").optional().attributes(key("etc").value("무통장 거래시 처리")),
                                        fieldWithPath("refundDepositorName").type(JsonFieldType.STRING).description("환불 은행 예금주").optional().attributes(key("etc").value("무통장 거래시 처리"))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
                ;

        // 결제취소 체크를 위한 조회
        OrderFindDto.Response nowDto = orderService.findOrder(OrderFindDto.Param.builder()
                .partnerCode("PT0000001")
                .orderNo("192211151341424534")
                .build());

        // 검증
        //assertEquals(cancelDto.getCancelReasonName(), nowDto.getPayment().getCancelReason());
        assertEquals(PaymentConst.Status.REQUEST_CANCEL_PAYMENT.getName(), nowDto.getPayment().getPaymentStatusName());
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("환불 계좌 조회")
    void findPaymentCancelReason() throws Exception {
        String orderNo = "192211151341424534";
        int orderProductSeq = 1;

        // 주문상품 취소 사유 API 조회
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/payments/{orderNo}/refund-account/{orderProductSeq}", orderNo, orderProductSeq)
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.TEXT_HTML)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("payment-refundAccount-find",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("orderNo").description("주문번호"),
                                        parameterWithName("orderProductSeq").description("주문상품 SEQ")
                                ),
                                responseFields(
                                        fieldWithPath("data.orderNo").type(JsonFieldType.STRING).description("주문번호"),
                                        fieldWithPath("data.orderProductSeq").type(JsonFieldType.NUMBER).description("주문상품 SEQ"),
                                        fieldWithPath("data.refundBankCode").type(JsonFieldType.STRING).description("은행 코드"),
                                        fieldWithPath("data.refundBankAccountNo").type(JsonFieldType.STRING).description("계좌번"),
                                        fieldWithPath("data.refundDepositorName").type(JsonFieldType.STRING).description("예금주"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
        ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("환불 계좌 수정")
    void updatePaymentCancelReason() throws Exception {
        PaymentRefundDto.ReqBody reasonDto = PaymentRefundDto.ReqBody.builder()
                .orderNo("192211151341424534")
                .orderProductSeq(1)
                .refundBankCode("90")
                .refundBankAccountNo("333333333")
                .refundDepositorName("홍길동")
                .build();

        // 주문상품 결제 취소 사유 수정 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/payments/{orderNo}/refund-account", reasonDto.getOrderNo())
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content((new ObjectMapper().writeValueAsString(reasonDto)))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("payment-refundAccount-update",
                                requestDocument(),
                                responseDocument(),
                                requestHeaders(
                                        headerWithName(AUTH_HEADER).description("JWT 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("orderNo").description("주문번호")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문번호").attributes(key("etc").value("")),
                                        fieldWithPath("orderProductSeq").type(JsonFieldType.NUMBER).description("주문상품 SEQ").attributes(key("etc").value("")),
                                        fieldWithPath("refundBankCode").type(JsonFieldType.STRING).description("은행 코드").attributes(key("etc").value("마스터코드 000046")),
                                        fieldWithPath("refundBankAccountNo").type(JsonFieldType.STRING).description("계좌번호").attributes(key("etc").value("")),
                                        fieldWithPath("refundDepositorName").type(JsonFieldType.STRING).description("예금주").attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드"),
                                        fieldWithPath("WBCommon.message").type(JsonFieldType.STRING).description("메시지")
                                )
                ))
        ;
    }

}
