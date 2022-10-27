package kr.wrightbrothers.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.RequestReturnUpdateDto;
import kr.wrightbrothers.apps.order.dto.ReturnListDto;
import kr.wrightbrothers.apps.order.dto.ReturnMemoUpdateDto;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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
        String orderNo = "202210271306217301";

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
                                        fieldWithPath("data.payment.paymentMethodCode").type(JsonFieldType.STRING).description("* 결제 수단 코드"),
                                        fieldWithPath("data.payment.paymentMethodName").type(JsonFieldType.STRING).description("결제 수단 이름"),
                                        fieldWithPath("data.payment.paymentStatusCode").type(JsonFieldType.STRING).description("* 결제 상태 코드"),
                                        fieldWithPath("data.payment.paymentStatusName").type(JsonFieldType.STRING).description("결제 상태 이름"),
                                        fieldWithPath("data.payment.cancelDate").type(JsonFieldType.STRING).description("취소 일시").optional(),
                                        fieldWithPath("data.payment.cancelReason").type(JsonFieldType.STRING).description("취소 사유").optional(),
                                        fieldWithPath("data.orderProductList[]").type(JsonFieldType.ARRAY).description("주문 상품 목록"),
                                        fieldWithPath("data.orderProductList[].orderProductSeq").type(JsonFieldType.NUMBER).description("주문 상품 SEQ"),
                                        fieldWithPath("data.orderProductList[].productCode").type(JsonFieldType.STRING).description("상품 코드"),
                                        fieldWithPath("data.orderProductList[].productName").type(JsonFieldType.STRING).description("상품 명"),
                                        fieldWithPath("data.orderProductList[].optionName").type(JsonFieldType.STRING).description("옵션 명"),
                                        fieldWithPath("data.orderProductList[].productQty").type(JsonFieldType.NUMBER).description("구매 수량"),
                                        fieldWithPath("data.orderProductList[].deliveryCompanyCode").type(JsonFieldType.STRING).optional().description("택배 업체 코드"),
                                        fieldWithPath("data.orderProductList[].deliveryCompanyName").type(JsonFieldType.STRING).optional().description("택배 업체 이름"),
                                        fieldWithPath("data.orderProductList[].invoiceNo").type(JsonFieldType.STRING).optional().description("송장 번호"),
                                        fieldWithPath("data.orderProductList[].deliveryStatusCode").type(JsonFieldType.STRING).optional().description("배송 상태 코드"),
                                        fieldWithPath("data.orderProductList[].deliveryStatusName").type(JsonFieldType.STRING).optional().description("배송 상태 이름"),
                                        fieldWithPath("data.orderProductList[].deliveryStartDay").type(JsonFieldType.STRING).optional().description("배송 시작 일자"),
                                        fieldWithPath("data.orderProductList[].deliveryEndDay").type(JsonFieldType.STRING).optional().description("배송 종료 일자"),
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
                                        fieldWithPath("data.returnProductList[].returnDeliveryCompanyCode").type(JsonFieldType.STRING).optional().description("반품 택배사 코드"),
                                        fieldWithPath("data.returnProductList[].returnDeliveryCompanyName").type(JsonFieldType.STRING).optional().description("반품 택배사 이름"),
                                        fieldWithPath("data.returnProductList[].returnInvoiceNo").type(JsonFieldType.STRING).optional().description("반품 송장 번호"),
                                        fieldWithPath("data.returnProductList[].reason").type(JsonFieldType.STRING).optional().description("사유"),
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("반품관리 정보 수정")
    void updateReturn() throws Exception {
        ReturnMemoUpdateDto updateParam = ReturnMemoUpdateDto.builder()
                .orderNo("202210271306217301")
                .recipientName("홍길동")
                .recipientPhone("01012341234")
                .recipientAddressZipCode("12345")
                .recipientAddress("서울특별시 강남구 강남대로 154길 37")
                .recipientAddressDetail("주경빌딩 2층")
                .returnMemo("반품메모")
                .build();

        // 반품관리 수정 API 테스트
        mockMvc.perform(put("/v1/returns")
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
                                        fieldWithPath("recipientName").type(JsonFieldType.STRING).description("수령자 이름").attributes(key("etc").value("")),
                                        fieldWithPath("recipientPhone").type(JsonFieldType.STRING).description("수령자 휴대전화").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddressZipCode").type(JsonFieldType.STRING).description("배송지 우편번호").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddress").type(JsonFieldType.STRING).description("배송지 주소").attributes(key("etc").value("")),
                                        fieldWithPath("recipientAddressDetail").type(JsonFieldType.STRING).description("배송지 상세주소").attributes(key("etc").value("")),
                                        fieldWithPath("returnMemo").type(JsonFieldType.STRING).description("반품 메모").optional().attributes(key("etc").value(""))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

    @Test
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    @DisplayName("반품 요청 상품 처리")
    void updateRequestReturn() throws Exception {
        RequestReturnUpdateDto updateParam = RequestReturnUpdateDto.builder()
                .orderNo("202210271306217301")
                .orderProductSeqArray(new Integer[]{1})
                .returnProcessCode(OrderStatusCode.START_RETURN.getCode())
                .requestCode("cgkwe")
                .requestName("대한통운")
                .requestValue("102849583785")
                .build();

        // 반품 요청 상품 처리 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/returns/{orderNo}/request-return", updateParam.getOrderNo())
                    .header(AUTH_HEADER, JWT_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(updateParam))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.WBCommon.state").value("S"))
                .andDo(
                        document("return-request-update",
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
                                        fieldWithPath("returnProcessCode").type(JsonFieldType.STRING).description("반품 처리 코드").attributes(key("etc").value("R03 반품승인, R02 반품취소, R05 반품완료, R04 반품불가")),
                                        fieldWithPath("requestCode").type(JsonFieldType.STRING).description("반품 요청 데이터 코드").optional().attributes(key("etc").value("000085 반품불가 사유, 000044 반품 진행 택배사")),
                                        fieldWithPath("requestName").type(JsonFieldType.STRING).description("반품 요청 데이터 이름").optional().attributes(key("etc").value("")),
                                        fieldWithPath("requestValue").type(JsonFieldType.STRING).description("반품 요청 데이터").optional().attributes(key("etc").value("반품 진행 송장번호"))
                                ),
                                responseFields(
                                        fieldWithPath("WBCommon.state").type(JsonFieldType.STRING).description("상태코드")
                                )
                ))
                ;
    }

}
