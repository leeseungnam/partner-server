package kr.wrightbrothers.apps.user;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.user.dto.UserInsertDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = {"회원"})
@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController extends WBController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "회원가입", notes = "회원가입 요청 API 입니다.")
    @PostMapping()
    public WBModel insertUser(@ApiParam @Valid @RequestBody UserInsertDto parmaDto) {

        // encoding password
        parmaDto.changePwd(passwordEncoder.encode(parmaDto.getUserPwd()));
        //setUserStatusCode
        parmaDto.changeUserStatusCode(PartnerKey.Code.User.Status.JOIN);

        userService.insertUser(parmaDto);

        return  noneDataResponse();
    }
}
