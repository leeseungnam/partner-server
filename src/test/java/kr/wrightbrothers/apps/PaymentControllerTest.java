package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.type.PaymentStatusCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.OrderFindDto;
import kr.wrightbrothers.apps.order.dto.PaymentCancelDto;
import kr.wrightbrothers.apps.order.service.OrderService;
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
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

public class PaymentControllerTest extends BaseControllerTests {

    @Autowired
    private OrderService orderService;

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("주문내역 상품 결제취소 요청")
    void updatePaymentCancel() throws Exception {
        PaymentCancelDto cancelDto = PaymentCancelDto.builder()
                .orderNo("202210271302094424")
                .orderProductSeq(new Integer[]{1})
                .cancelReasonCode("C01")
                .cancelReasonName("구매 의사 취소")
                .paymentMethodCode(PaymentMethodCode.CARD.getCode())
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
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;

        // 결제취소 체크를 위한 조회
        OrderFindDto.Response nowDto = orderService.findOrder(OrderFindDto.Param.builder()
                .partnerCode("PT0000001")
                .orderNo("202210271302094424")
                .build());

        // 검증
        assertEquals(cancelDto.getCancelReasonName(), nowDto.getPayment().getCancelReason());
        assertEquals(PaymentStatusCode.REQUEST_CANCEL_PAYMENT.getName(), nowDto.getPayment().getPaymentStatusName());

    }

}
