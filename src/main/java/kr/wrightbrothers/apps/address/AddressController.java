package kr.wrightbrothers.apps.address;

import kr.wrightbrothers.apps.address.dto.*;
import kr.wrightbrothers.apps.address.service.AddressService;
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
public class AddressController extends WBController {

    private final AddressService addressService;

    @GetMapping("/addresses")
    public WBModel findAddressList(@RequestParam int count,
                                   @RequestParam int page,
                                   @AuthenticationPrincipal UserPrincipal user) {
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

    @PostMapping("/addresses")
    public WBModel insertAddress(@RequestBody AddressInsertDto paramDto,
                                 @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.setPartnerCode(user.getUserAuth().getPartnerCode());

        // 주소록 등록
        addressService.insertAddress(paramDto);

        return noneDataResponse();
    }

    @GetMapping("/addresses/{addressNo}")
    public WBModel findAddress(@PathVariable Long addressNo,
                               @AuthenticationPrincipal UserPrincipal user) {
        return defaultResponse(
                addressService.findAddress(
                        AddressFindDto.Param.builder()
                                .partnerCode(user.getUserAuth().getPartnerCode())
                                .addressNo(addressNo)
                                .build())
        );
    }

    @PutMapping("/addresses")
    public WBModel updateAddress(@RequestBody AddressUpdateDto paramDto,
                                 @AuthenticationPrincipal UserPrincipal user) {
        // Security Custom UserDetail 객체를 통해 파트너 코드, 아이디 정보 추출
        paramDto.setUserId(user.getUsername());
        paramDto.setPartnerCode(user.getUserAuth().getPartnerCode());

        // 주소록 수정
        addressService.updateAddress(paramDto);

        return noneDataResponse();
    }

    @DeleteMapping("/addresses")
    public WBModel deleteAddress(@RequestParam Long addressNo,
                                 @AuthenticationPrincipal UserPrincipal user) {
        // 주소록 삭제
        addressService.deleteAddress(
                AddressDeleteDto.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .addressNo(addressNo)
                        .build());

        return noneDataResponse();
    }

}
