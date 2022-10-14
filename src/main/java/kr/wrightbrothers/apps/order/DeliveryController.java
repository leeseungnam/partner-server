package kr.wrightbrothers.apps.order;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.order.service.DeliveryService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = {"배송"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class DeliveryController extends WBController {

    private final DeliveryService deliveryService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "배송관리 목록 조회", notes = "주문 정보의 배송 목록 조회")
    @GetMapping(value = {
            "/deliveries",
            "/deliveries/status-statistics"
    })
    public WBModel findDeliveryList(@ApiParam(value = "배송상태") @RequestParam String[] deliveryStatus,
                                    @ApiParam(value = "배송방법") @RequestParam String[] deliveryType,
                                    @ApiParam(value = "조회기간 시작일") @RequestParam String startDay,
                                    @ApiParam(value = "조회기간 종료일") @RequestParam String endDay,
                                    @ApiParam(value = "키워드 구분") @RequestParam String keywordType,
                                    @ApiParam(value = "키워드 값") @RequestParam(required = false) String keywordValue,
                                    @ApiParam(value = "페이지 행 수") @RequestParam int count,
                                    @ApiParam(value = "현재 페이지") @RequestParam int page,
                                    @ApiIgnore @AuthenticationPrincipal UserPrincipal user,
                                    @ApiIgnore HttpServletRequest request
    ) {
        WBModel response = new WBModel();
        DeliveryListDto.Param paramDto = DeliveryListDto.Param.builder()
                .partnerCode(user.getUserAuth().getPartnerCode())
                .deliveryType(deliveryType)
                .deliveryStatus(deliveryStatus)
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "배송 관리 상세 정보 조회", notes = "주문 내역의 및 배송에 대한 상세 정보 조회")
    @GetMapping("/deliveries/{orderNo}")
    public WBModel findDelivery(@ApiParam(value = "주문번호") @PathVariable String orderNo,
                                @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        // 배송 관리 상세 정보 조회
        return defaultResponse(deliveryService.findDelivery(
                DeliveryFindDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .build()
        ));
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "배송 정보 수정", notes = "배송지 정보, 배송 메모에 대한 정보 수정")
    @PutMapping("/deliveries")
    public WBModel updateDelivery(@ApiParam(value = "배송 수정 데이터") @Valid @RequestBody DeliveryMemoUpdateDto paramDto) {
        // 배송관리 정보 수정
        deliveryService.updateDelivery(paramDto);

        return noneDataResponse();
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "송장번호 저장", notes = "택배 발송되는 주문 상품의 택배사 / 송장번호 등록 및 수정")
    @PutMapping("/deliveries/{orderNo}/invoice")
    public WBModel updateDeliveryInvoice(@ApiParam(value = "송장번호 입력 데이터") @Valid @RequestBody DeliveryInvoiceUpdateDto paramDto) {
        // 배송정보 수정
        deliveryService.updateDeliveryInvoice(paramDto);

        return noneDataResponse();
    }

}
