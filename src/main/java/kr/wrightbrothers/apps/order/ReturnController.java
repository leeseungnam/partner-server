package kr.wrightbrothers.apps.order;

import kr.wrightbrothers.apps.order.dto.OrderFindDto;
import kr.wrightbrothers.apps.order.dto.ReturnListDto;
import kr.wrightbrothers.apps.order.service.OrderService;
import kr.wrightbrothers.apps.order.service.ReturnService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ReturnController extends WBController {

    private final ReturnService returnService;
    private final OrderService orderService;

    @GetMapping(value = {
            "/returns"
    })
    public WBModel findReturnList(@RequestParam String[] returnStatus,
                                  @RequestParam String rangeType,
                                  @RequestParam String startDay,
                                  @RequestParam String endDay,
                                  @RequestParam String keywordType,
                                  @RequestParam(required = false) String keywordValue,
                                  @RequestParam int count,
                                  @RequestParam int page,
                                  @AuthenticationPrincipal UserPrincipal user
    ) {
        WBModel response = new WBModel();
        ReturnListDto.Param paramDto = ReturnListDto.Param.builder()
                .partnerCode(user.getUserAuth().getPartnerCode())
                .returnStatus(returnStatus)
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

        // 반품 내역 목록 조회
        response.addObject(WBKey.WBModel.DefaultDataKey, returnService.findReturnList(paramDto));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

    @GetMapping("/returns/{orderNo}")
    public WBModel findReturn(@PathVariable String orderNo,
                              @AuthenticationPrincipal UserPrincipal user) {
        // 반품 관리 상세 정보 조회
        // 주문 내역 정보와 동일한 데이터를 처리하므로 해당 서비스 이용.
        // 이후 확장에 대한 변동이 생길 경우 서비스 로직 추가 필요 함.
        return defaultResponse(orderService.findOrder(
                OrderFindDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .build()));
    }

}
