package kr.wrightbrothers.apps.order;

import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.order.dto.DeliveryInvoiceUpdateDto;
import kr.wrightbrothers.apps.order.dto.DeliveryListDto;
import kr.wrightbrothers.apps.order.dto.OrderFindDto;
import kr.wrightbrothers.apps.order.dto.OrderUpdateDto;
import kr.wrightbrothers.apps.order.service.DeliveryService;
import kr.wrightbrothers.apps.order.service.OrderService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class DeliveryController extends WBController {

    private final DeliveryService deliveryService;
    private final OrderService orderService;

    @GetMapping(value = {
            "/deliveries"
    })
    public WBModel findDeliveryList(@RequestParam String[] deliveryStatus,
                                    @RequestParam String[] deliveryType,
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
        DeliveryListDto.Param paramDto = DeliveryListDto.Param.builder()
                .partnerCode(user.getUserAuth().getPartnerCode())
                .deliveryType(deliveryType)
                .deliveryStatus(deliveryStatus)
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

        // 배송 상태별 집계 건수 조회
        if (request.getRequestURI().contains("statistics")) {
            return defaultResponse(deliveryService.findDeliveryStatusStatistics(paramDto));
        }

       // 배송 내역 목록 조회
        response.addObject(WBKey.WBModel.DefaultDataKey, deliveryService.findDeliveryList(paramDto));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

    @GetMapping("/deliveries/{orderNo}")
    public WBModel findDelivery(@PathVariable String orderNo,
                                @AuthenticationPrincipal UserPrincipal user) {
        // 반품관리 배송 상세 정보 조회
        // 주문 내역 정보와 동일한 데이터를 처리하므로 해당 서비스 이용.
        // 이후 확장에 대한 변동이 생길 경우 서비스 로직 추가 필요 함.
        return defaultResponse(orderService.findOrder(
                OrderFindDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .build()));
    }

    @UserPrincipalScope
    @PutMapping("/deliveries")
    public WBModel updateDelivery(@Valid @RequestBody OrderUpdateDto paramDto) {
        // 주문 정보 수정
        // 주문 내역 정보와 동일한 데이터 수정 처리를 하므로 해당 서비스 이용.
        // 이후 확장에 대한 변동이 생길 경우 서비스 로직 추가 필요 함.
        orderService.updateOrder(paramDto);

        return noneDataResponse();
    }

    @PutMapping("/deliveries/{orderNo}/invoice")
    public WBModel updateDeliveryInvoice(@Valid @RequestBody DeliveryInvoiceUpdateDto paramDto) {
        // 배송정보 수정
        deliveryService.updateDeliveryInvoice(paramDto);

        return noneDataResponse();
    }

}
