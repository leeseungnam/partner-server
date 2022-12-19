package kr.wrightbrothers.apps.address;

import kr.wrightbrothers.apps.address.dto.*;
import kr.wrightbrothers.apps.address.service.AddressService;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AddressController extends WBController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final AddressService addressService;

    @GetMapping("/addresses")
    public WBModel findAddressList(@RequestParam(required = false, defaultValue = "2") int count,
                                   @RequestParam(required = false, defaultValue = "1") int page,
                                   @RequestParam(required = false, defaultValue = "ALL") String searchType,
                                   @AuthenticationPrincipal UserPrincipal user) {
        WBModel response = new WBModel();
        AddressListDto.Param paramDto = AddressListDto.Param.builder()
                .partnerCode(user.getUserAuth().getPartnerCode())
                .count(count)
                .page(page)
                .searchType(searchType)
                .build();

        // 주소록 목록 조회
        response.addObject(WBKey.WBModel.DefaultDataKey, addressService.findAddressList(paramDto));
        response.addObject(WBKey.WBModel.DefaultDataTotalCountKey, paramDto.getTotalItems());

        return response;
    }

    @UserPrincipalScope
    @PostMapping("/addresses")
    public WBModel insertAddress(@Valid @RequestBody AddressInsertDto paramDto) {
        // 주소록 등록
        addressService.insertAddress(paramDto);

        return insertMsgResponse(messageSourceAccessor);
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

    @UserPrincipalScope
    @PutMapping("/addresses")
    public WBModel updateAddress(@Valid @RequestBody AddressUpdateDto paramDto) {
        // 주소록 수정
        addressService.updateAddress(paramDto);

        return noneMgsResponse(messageSourceAccessor);
    }

    @DeleteMapping("/addresses")
    public WBModel deleteAddress(@RequestParam Long addressNo,
                                 @ApiIgnore @AuthenticationPrincipal UserPrincipal user) {
        // 주소록 삭제
        addressService.deleteAddress(
                AddressDeleteDto.builder()
                        .partnerCode(user.getUserAuth().getPartnerCode())
                        .addressNo(addressNo)
                        .build());

        return noneMgsResponse(messageSourceAccessor);
    }

}
