package kr.wrightbrothers.apps.address;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.address.dto.*;
import kr.wrightbrothers.apps.address.service.AddressService;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = {"주소록"})
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AddressController extends WBController {

    private final AddressService addressService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "주소록 목록 조회", notes = "등록된 주소록의 목록 조회")
    @GetMapping("/addresses")
    public WBModel findAddressList(@ApiParam(value = "페이지 행 수") @RequestParam int count,
                                   @ApiParam(value = "현재 페이지") @RequestParam int page,
                                   @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        WBModel response = new WBModel();
        AddressListDto.Param paramDto = AddressListDto.Param.builder()
                .partnerCode(user.getUserAuth().getPartnerCode())
                .count(count)
                .page(page)
                .build();

        // 주소록 목록 조회
        response.addObject(WBKey.WBModel.DefaultDataKey, addressService.findAddressList(paramDto));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "주소록 등록", notes = "주소록 등록 기능 제공")
    @PostMapping("/addresses")
    public WBModel insertAddress(@ApiParam(value = "주소록 등록 데이터") @Valid @RequestBody AddressInsertDto paramDto,
                                 @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.setPartnerCode(user.getUserAuth().getPartnerCode());

        // 주소록 등록
        addressService.insertAddress(paramDto);

        return noneDataResponse();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "주소록 조회", notes = "주소록 상세 내용 조회")
    @GetMapping("/addresses/{addressNo}")
    public WBModel findAddress(@ApiParam(value = "주소록 번호") @PathVariable Long addressNo,
                               @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(
                addressService.findAddress(
                        AddressFindDto.Param.builder()
                                .partnerCode(user.getUserAuth().getPartnerCode())
                                .addressNo(addressNo)
                                .build())
        );
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "주소록 수정", notes = "등록된 주소록 정보 수정")
    @PutMapping("/addresses")
    public WBModel updateAddress(@ApiParam(value = "주소록 수정 데이터") @Valid @RequestBody AddressUpdateDto paramDto,
                                 @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.setPartnerCode(user.getUserAuth().getPartnerCode());

        // 주소록 수정
        addressService.updateAddress(paramDto);

        return noneDataResponse();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = "토큰", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "주소록 삭제", notes = "등록된 주소록 정보 삭제")
    @DeleteMapping("/addresses")
    public WBModel deleteAddress(@ApiParam(value = "주소록 번호") @RequestParam Long addressNo,
                                 @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        // 주소록 삭제
        addressService.deleteAddress(
                AddressDeleteDto.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .addressNo(addressNo)
                        .build());

        return noneDataResponse();
    }

}
