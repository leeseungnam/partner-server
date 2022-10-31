package kr.wrightbrothers.apps.order;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.order.dto.*;
import kr.wrightbrothers.apps.order.service.ReturnService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"반품"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ReturnController extends WBController {

    private final ReturnService returnService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "반품내역 목록 조회", notes = "반품 정보의 목록 조회")
    @GetMapping(value = {
            "/returns"
    })
    public WBModel findReturnList(@ApiParam(value = "반품상태") @RequestParam String[] returnStatus,
                                  @ApiParam(value = "조회기간 구분") @RequestParam String rangeType,
                                  @ApiParam(value = "조회기간 시작일") @RequestParam String startDay,
                                  @ApiParam(value = "조회기간 종료일") @RequestParam String endDay,
                                  @ApiParam(value = "키워드 구분") @RequestParam String keywordType,
                                  @ApiParam(value = "키워드 값") @RequestParam(required = false) String keywordValue,
                                  @ApiParam(value = "페이지 행 수") @RequestParam int count,
                                  @ApiParam(value = "현재 페이지") @RequestParam int page,
                                  @ApiIgnore @AuthenticationPrincipal UserPrincipal user
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
    public void returnExcelDownload(@ApiParam(value = "반품상태") @RequestParam String[] returnStatus,
                                    @ApiParam(value = "조회기간 구분") @RequestParam String rangeType,
                                    @ApiParam(value = "조회기간 시작일") @RequestParam String startDay,
                                    @ApiParam(value = "조회기간 종료일") @RequestParam String endDay,
                                    @ApiParam(value = "키워드 구분") @RequestParam String keywordType,
                                    @ApiParam(value = "키워드 값") @RequestParam(required = false) String keywordValue,
                                    @ApiParam(value = "페이지 행 수") @RequestParam int count,
                                    @ApiParam(value = "현재 페이지") @RequestParam int page,
                                    @ApiIgnore @AuthenticationPrincipal UserPrincipal user,
                                    @ApiIgnore HttpServletResponse response
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "반품내역 조회", notes = "반품내역 상세 정보 조회")
    @GetMapping("/returns/{orderNo}")
    public WBModel findReturn(@ApiParam(value = "주문 번호") @PathVariable String orderNo,
                              @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(returnService.findReturn(
                ReturnFindDto.Param.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .orderNo(orderNo)
                        .build()
        ));
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "반품내역 수정", notes = "반품내역의 배송지 정보, 반품 메모의 내용을 수정")
    @PutMapping("/returns")
    public WBModel updateReturn(@ApiParam(value = "반품내역 수정 데이터") @Valid @RequestBody ReturnMemoUpdateDto paramDto) {
        // 반품관리 정보 수정
        returnService.updateReturn(paramDto);

        return noneDataResponse();
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "반품요청 처리", notes = "고객 반품 요청에 대한 처리 수행")
    @PutMapping("/returns/{orderNo}/request-return")
    public WBModel updateRequestReturn(@ApiParam(value = "반품요청 처리 데이터") @Valid @RequestBody RequestReturnUpdateDto paramDto) {
        // 반품 요청에 대한 처리 수행
        returnService.updateRequestReturn(paramDto);

        return noneDataResponse();
    }

}
