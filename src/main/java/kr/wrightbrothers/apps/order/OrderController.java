package kr.wrightbrothers.apps.order;

import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.order.service.OrderService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController extends WBController {

    private final OrderService orderService;
    private final MessageSourceAccessor messageSourceAccessor;

    @GetMapping(value = {
            "/orders",
            "/orders/status-statistics"
    })
    public WBModel findOrderList(@RequestParam(required = false) String[] orderStatus,
                                 @RequestParam(required = false) String[] paymentStatus,
                                 @RequestParam(required = false) String[] paymentMethod,
                                 @RequestParam(required = false) String rangeType,
                                 @RequestParam(required = false) String startDay,
                                 @RequestParam(required = false) String endDay,
                                 @RequestParam String keywordType,
                                 @RequestParam(required = false) String keywordValue,
                                 @RequestParam String sortType,
                                 @RequestParam(required = false) int count,
                                 @RequestParam(required = false) int page,
                                 @AuthenticationPrincipal UserPrincipal user,
                                 HttpServletRequest request
    ) {
        WBModel response = new WBModel();
        OrderListDto.Param paramDto = OrderListDto.Param.builder()
                .partnerCode(user.getUserAuth().getPartnerCode())
                .orderStatus(orderStatus)
                .paymentStatus(paymentStatus)
                .paymentMethod(paymentMethod)
                .rangeType(rangeType)
                .startDay(startDay)
                .endDay(endDay)
                .keywordType(keywordType)
                .keywordValue(keywordValue)
                .sortType(sortType)
                .count(count)
                .page(page)
                .build();
        // 파라미터 초기설정 처리(다건검색, 반품 상태값 추가)
        paramDto.parameterInit();

        // 주문내역 주문 집계 건수 조회
        if (request.getRequestURI().contains("statistics")) {
            return defaultResponse(orderService.findOrderStatusStatistics(paramDto));
        }

        // 주문 내역 목록 조회
        response.addObject(WBKey.WBModel.DefaultDataKey, orderService.findOrderList(paramDto, true));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

    @GetMapping("/orders/excel")
    public void orderExcelDownload(@RequestParam(required = false) String[] orderStatus,
                                   @RequestParam(required = false) String[] paymentStatus,
                                   @RequestParam(required = false) String[] paymentMethod,
                                   @RequestParam(required = false) String rangeType,
                                   @RequestParam(required = false) String startDay,
                                   @RequestParam(required = false) String endDay,
                                   @RequestParam String keywordType,
                                   @RequestParam(required = false) String keywordValue,
                                   @RequestParam String sortType,
                                   @RequestParam(required = false) int count,
                                   @RequestParam(required = false) int page,
                                   @AuthenticationPrincipal UserPrincipal user,
                                   HttpServletResponse response
    ) throws IOException {
        OrderListDto.Param paramDto = OrderListDto.Param.builder()
                .partnerCode(user.getUserAuth().getPartnerCode())
                .orderStatus(orderStatus)
                .paymentStatus(paymentStatus)
                .paymentMethod(paymentMethod)
                .rangeType(rangeType)
                .startDay(startDay)
                .endDay(endDay)
                .keywordType(keywordType)
                .keywordValue(keywordValue)
                .sortType(sortType)
                .count(count)
                .page(page)
                .build();
        // 파라미터 초기설정 처리(다건검색, 반품 상태값 추가)
        paramDto.parameterInit();

        // 주문 내역 목록 조회
        List<OrderListDto.Response> orderList = orderService.findOrderList(paramDto, false);

        // 엑셀 다운로드
        orderService.makeExcelFile(
                OrderExcelDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNoList(orderList.stream()
                                .map(OrderListDto.Response::getOrderNo)
                                .collect(Collectors.toList()))
                        .build(),
                response
        );
    }

    @GetMapping("/orders/{orderNo}")
    public WBModel findOrder(@PathVariable String orderNo,
                             @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(orderService.findOrder(
                OrderFindDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .build()
                ));
    }

    @UserPrincipalScope
    @PutMapping("/orders")
    public WBModel updateOrder(@Valid @RequestBody OrderMemoUpdateDto paramDto) {
        // 주문 정보 수정
        orderService.updateOrderMemo(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @PatchMapping("/orders/preparing-deliveries")
    public WBModel updatePreparingDelivery(@Valid @RequestBody DeliveryPreparingDto paramDto) {
        // 상품 준비중 상태 변경
        orderService.updatePreparingDelivery(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

}
