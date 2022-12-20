package kr.wrightbrothers.apps.order;

import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.order.service.ReturnService;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ReturnController extends WBController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final ReturnService returnService;

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

    @GetMapping("/returns/excel")
    public void returnExcelDownload(@RequestParam String[] returnStatus,
                                    @RequestParam String rangeType,
                                    @RequestParam String startDay,
                                    @RequestParam String endDay,
                                    @RequestParam String keywordType,
                                    @RequestParam(required = false) String keywordValue,
                                    @RequestParam int count,
                                    @RequestParam int page,
                                    @AuthenticationPrincipal UserPrincipal user,
                                    HttpServletResponse response
    ) throws IOException {
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
        List<ReturnListDto.Response> returnList = returnService.findReturnList(paramDto);

        if (ObjectUtils.isEmpty(returnList))
            throw new WBBusinessException(ErrorCode.NO_CONTENT.getErrCode(), new String[]{"반품 목록"});

        // 엑셀 다운로드
        returnService.makeExcelFile(
                ReturnExcelDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .returnList(returnList.stream()
                                .map(ReturnListDto.Response::getOrderNo)
                                .collect(Collectors.toList()))
                        .build(),
                response
        );
    }

    @GetMapping("/returns/{orderNo}")
    public WBModel findReturn(@PathVariable String orderNo,
                              @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(returnService.findReturn(
                ReturnFindDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .build()
        ));
    }

    @UserPrincipalScope
    @PatchMapping("/returns")
    public WBModel updateReturnMemo(@Valid @RequestBody ReturnMemoUpdateDto paramDto) {
        // 반품관리 정보 수정
        returnService.updateReturnMemo(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @PutMapping("/returns/{orderNo}/approval-return")
    public WBModel updateApprovalReturn(@Valid @RequestBody ApprovalReturnDto paramDto) {
        // 반품 요청에 대한 승인
        returnService.updateRequestReturn(paramDto.toRequestReturnUpdateDto());

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @PutMapping("/returns/{orderNo}/cancel-return")
    public WBModel updateCancelReturn(@Valid @RequestBody CancelReturnDto paramDto) {
        // 반품 취소에 대한 처리
        returnService.updateRequestReturn(paramDto.toRequestReturnUpdateDto(OrderConst.ProductStatus.WITHDRAWAL_RETURN.getCode()));

        return defaultMsgResponse(messageSourceAccessor, "return.cancel", null);
    }

    @UserPrincipalScope
    @PutMapping("/returns/{orderNo}/complete-return")
    public WBModel updateCompleteReturn(@Valid @RequestBody RequestReturnDto paramDto) {
        // 유효성 체크
        paramDto.valid();

        // 반품 완료에 대한 처리
        returnService.updateRequestReturn(paramDto.toRequestReturnUpdateDto(OrderConst.ProductStatus.REQUEST_COMPLETE_RETURN.getCode()));

        return noneMgsResponse(messageSourceAccessor);
    }

    @UserPrincipalScope
    @PutMapping("/returns/{orderNo}/non-return")
    public WBModel updateNonReturn(@Valid @RequestBody NonReturnDto paramDto) {
        // 반품 불가 처리
        returnService.updateRequestReturn(paramDto.toRequestReturnUpdateDto());

        return noneMgsResponse(messageSourceAccessor);
    }

    @GetMapping("/returns/{orderNo}/deliveries/{orderProductSeq}")
    public WBModel findReturnDelivery(@PathVariable String orderNo,
                                      @PathVariable Integer orderProductSeq,
                                      @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        // 반품 배송정보 조회
        return defaultResponse(returnService.findReturnDelivery(
                ReturnDeliveryDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .orderProductSeq(orderProductSeq)
                        .userId(user.getUsername())
                        .build()
        ));
    }

    @UserPrincipalScope
    @PutMapping("/returns/{orderNo}/deliveries")
    public WBModel updateReturnDelivery(@Valid @RequestBody ReturnDeliveryDto.ReqBody paramDto) {
        // 반품 배송정보 수정
        returnService.updateReturnDelivery(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

}
