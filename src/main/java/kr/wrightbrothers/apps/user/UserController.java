package kr.wrightbrothers.apps.user;

import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
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

@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController extends WBController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping()
    public WBModel insertUser(@RequestBody UserInsertDto parmaDto) {

        // encoding password
        parmaDto.changePwd(passwordEncoder.encode(parmaDto.getUserPwd()));
        //setUserStatusCode
        parmaDto.changeUserStatusCode(PartnerKey.Code.User.Status.JOIN);

        userService.insertUser(parmaDto);

        return  noneDataResponse();
    }
}
