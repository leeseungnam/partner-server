package kr.wrightbrothers.apps.order;

import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.order.dto.RequestReturnUpdateDto;
import kr.wrightbrothers.apps.order.dto.ReturnFindDto;
import kr.wrightbrothers.apps.order.dto.ReturnListDto;
import kr.wrightbrothers.apps.order.dto.ReturnMemoUpdateDto;
import kr.wrightbrothers.apps.order.service.ReturnService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ReturnController extends WBController {

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
    @PutMapping("/returns")
    public WBModel updateReturn(@Valid @RequestBody ReturnMemoUpdateDto paramDto) {
        // 반품관리 정보 수정
        returnService.updateReturn(paramDto);

        return noneDataResponse();
    }

    @UserPrincipalScope
    @PutMapping("/returns/{orderNo}/request-return")
    public WBModel updateRequestReturn(@Valid @RequestBody RequestReturnUpdateDto paramDto) {
        // 반품 요청에 대한 처리 수행
        returnService.updateRequestReturn(paramDto);

        return noneDataResponse();
    }

}
