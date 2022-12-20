package kr.wrightbrothers.apps.order;

import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.order.service.DeliveryService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class DeliveryController extends WBController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final DeliveryService deliveryService;

    @GetMapping("/deliveries")
    public WBModel findDeliveryList(@RequestParam String[] deliveryStatus,
                                    @RequestParam String[] deliveryType,
                                    @RequestParam String startDay,
                                    @RequestParam String endDay,
                                    @RequestParam String keywordType,
                                    @RequestParam(required = false) String keywordValue,
                                    @RequestParam int count,
                                    @RequestParam int page,
                                    @AuthenticationPrincipal UserPrincipal user
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

       // 배송 내역 목록 조회
        response.addObject(WBKey.WBModel.DefaultDataKey, deliveryService.findDeliveryList(paramDto, true));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

    @GetMapping("/deliveries/excel")
    public void deliveryExcelDownload(@RequestParam String[] deliveryStatus,
                                      @RequestParam String[] deliveryType,
                                      @RequestParam String startDay,
                                      @RequestParam String endDay,
                                      @RequestParam String keywordType,
                                      @RequestParam(required = false) String keywordValue,
                                      @RequestParam int count,
                                      @RequestParam int page,
                                      @AuthenticationPrincipal UserPrincipal user,
                                      HttpServletResponse response
    ) throws IOException {
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

        // 배송 내역 목록 조회
        List<DeliveryListDto.Response> deliveryList = deliveryService.findDeliveryList(paramDto, false);

        // 엑셀 다운로드
        deliveryService.makeExcelFile(
                DeliveryExcelDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .deliveryList(deliveryList.stream()
                                .map(DeliveryListDto.Response::getOrderNo)
                                .collect(Collectors.toList()))
                        .build(),
                response
        );
    }

    @GetMapping("/deliveries/{orderNo}")
    public WBModel findDelivery(@PathVariable String orderNo,
                                @AuthenticationPrincipal UserPrincipal user) {
        // 배송 관리 상세 정보 조회
        return defaultResponse(deliveryService.findDelivery(
                DeliveryFindDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .build()
        ));
    }

    @UserPrincipalScope
    @PatchMapping("/deliveries")
    public WBModel updateDeliveryMemo(@Valid @RequestBody DeliveryMemoUpdateDto paramDto) {
        // 배송관리 정보 수정
        deliveryService.updateDeliveryMemo(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @PutMapping("/deliveries/{orderNo}/invoice")
    public WBModel updateDeliveryInvoice(@Valid @RequestBody DeliveryInvoiceUpdateDto paramDto) {
        // 송장번호 수정
        deliveryService.updateDeliveryInvoice(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @PutMapping("/deliveries/{orderNo}/freights")
    public WBModel updateDeliveryFreight(@Valid @RequestBody DeliveryFreightUpdateDto paramDto) {
        // 화물배송 등록
        deliveryService.updateDeliveryFreight(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @PutMapping("/deliveries/{orderNo}/pickup")
    public WBModel updateDeliveryPickup(@Valid @RequestBody DeliveryPickupUpdateDto paramDto) {
        // 방문수령 등록
        deliveryService.updateDeliveryPickup(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @PutMapping("/deliveries")
    public WBModel updateDelivery(@Valid @RequestBody DeliveryUpdateDto paramDto) {
        // 배송정보 수정
        deliveryService.updateDelivery(paramDto);

        return defaultMsgResponse(messageSourceAccessor, "order.save.success", null);
    }


    @GetMapping("/deliveries/{orderNo}/addresses/{orderProductSeq}")
    public WBModel findDeliveryAddresses(@PathVariable String orderNo,
                                         @PathVariable Integer orderProductSeq,
                                         @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(
                deliveryService.findDeliveryAddresses(
                        DeliveryAddressDto.Param.builder()
                                .partnerCode(user.getUserAuth().getPartnerCode())
                                .orderNo(orderNo)
                                .orderProductSeq(orderProductSeq)
                                .build())
        );
    }

}
