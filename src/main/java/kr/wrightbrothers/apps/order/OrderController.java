package kr.wrightbrothers.apps.order;

import kr.wrightbrothers.apps.order.dto.OrderListDto;
import kr.wrightbrothers.apps.order.service.OrderService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController extends WBController {

    private final OrderService orderService;

    @GetMapping(value = {
            "/orders",
            "/orders/status-statistics"
    })
    public WBModel findOrderList(@RequestParam String[] orderStatus,
                                 @RequestParam String[] paymentStatus,
                                 @RequestParam String[] paymentMethod,
                                 @RequestParam String rangeType,
                                 @RequestParam String startDay,
                                 @RequestParam String endDay,
                                 @RequestParam String keywordType,
                                 @RequestParam String keywordValue,
                                 @RequestParam int count,
                                 @RequestParam int page,
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
                .count(count)
                .page(page)
                .build();
        // 다건 검색조회 split 처리
        paramDto.splitKeywordValue();

        // 주문내역 주문 집계 건수 조회
        if (request.getRequestURI().contains("statistics")) {
            return defaultResponse(orderService.findOrderStatusStatistics(paramDto));
        }

        // 주문 내역 목록 조회
        response.addObject(WBKey.WBModel.DefaultDataKey, orderService.findOrderList(paramDto));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

}
