package kr.wrightbrothers.apps.order;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.order.service.OrderService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"주문"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController extends WBController {

    private final OrderService orderService;
    private final MessageSourceAccessor messageSourceAccessor;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "주문내역 목록 조회", notes = "주문 정보의 목록 조회")
    @GetMapping(value = {
            "/orders",
            "/orders/status-statistics"
    })
    public WBModel findOrderList(@ApiParam(value = "주문상태") @RequestParam String[] orderStatus,
                                 @ApiParam(value = "결제상태") @RequestParam String[] paymentStatus,
                                 @ApiParam(value = "결제수단") @RequestParam String[] paymentMethod,
                                 @ApiParam(value = "조회기간 구분") @RequestParam String rangeType,
                                 @ApiParam(value = "조회기간 시작일") @RequestParam String startDay,
                                 @ApiParam(value = "조회기간 종료일") @RequestParam String endDay,
                                 @ApiParam(value = "키워드 구분") @RequestParam String keywordType,
                                 @ApiParam(value = "키워드 값") @RequestParam(required = false) String keywordValue,
                                 @ApiParam(value = "정렬 타입") @RequestParam String sortType,
                                 @ApiParam(value = "페이지 행 수") @RequestParam(required = false) int count,
                                 @ApiParam(value = "현재 페이지") @RequestParam(required = false) int page,
                                 @ApiIgnore @AuthenticationPrincipal UserPrincipal user,
                                 @ApiIgnore HttpServletRequest request
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
        response.addObject(WBKey.WBModel.DefaultDataKey, orderService.findOrderList(paramDto));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

    @GetMapping("/orders/excel")
    public void orderExcelDownload(@ApiParam(value = "주문상태") @RequestParam String[] orderStatus,
                                   @ApiParam(value = "결제상태") @RequestParam String[] paymentStatus,
                                   @ApiParam(value = "결제수단") @RequestParam String[] paymentMethod,
                                   @ApiParam(value = "조회기간 구분") @RequestParam String rangeType,
                                   @ApiParam(value = "조회기간 시작일") @RequestParam String startDay,
                                   @ApiParam(value = "조회기간 종료일") @RequestParam String endDay,
                                   @ApiParam(value = "키워드 구분") @RequestParam String keywordType,
                                   @ApiParam(value = "키워드 값") @RequestParam(required = false) String keywordValue,
                                   @ApiParam(value = "정렬 타입") @RequestParam String sortType,
                                   @ApiParam(value = "페이지 행 수") @RequestParam(required = false) int count,
                                   @ApiParam(value = "현재 페이지") @RequestParam(required = false) int page,
                                   @ApiIgnore @AuthenticationPrincipal UserPrincipal user,
                                   @ApiIgnore HttpServletResponse response
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
        List<OrderListDto.Response> orderList = orderService.findOrderList(paramDto);

        if (ObjectUtils.isEmpty(orderList))
            throw new WBBusinessException(ErrorCode.NO_CONTENT.getErrCode(), new String[]{"주문 목록"});

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

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "주문내역 조회", notes = "주문내역 상세 정보 조회")
    @GetMapping("/orders/{orderNo}")
    public WBModel findOrder(@ApiParam(value = "주문 번호") @PathVariable String orderNo,
                             @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(orderService.findOrder(
                OrderFindDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .build()
                ));
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "주문내역 수정", notes = "주문내역의 메모의 내용을 수정")
    @PutMapping("/orders")
    public WBModel updateOrder(@ApiParam(value = "주문내역 수정 데이터") @Valid @RequestBody OrderMemoUpdateDto paramDto) {
        // 주문 정보 수정
        orderService.updateOrder(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "주문정보의 상태를 상품 준비중 단계로 변경", notes = "주문정보의 상태를 상품 준비중 단계로 변경 처리")
    @PatchMapping("/orders/preparing-deliveries")
    public WBModel updatePreparingDelivery(@ApiParam(value = "상품 준비중 변경 데이터") @Valid @RequestBody DeliveryPreparingDto paramDto) {
        // 상품 준비중 상태 변경
        orderService.updatePreparingDelivery(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

}
