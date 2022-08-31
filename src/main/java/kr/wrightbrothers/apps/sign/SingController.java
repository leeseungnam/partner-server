package kr.wrightbrothers.apps.sign;

import kr.wrightbrothers.apps.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.sign.dto.SignDto;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SingController extends WBController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/v1/login")
    public WBModel signIn(@RequestBody SignDto paramDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(paramDto.getUsrId(), paramDto.getUsrPw());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        WBModel response = new WBModel();
        response.addObject(WBKey.Jwt.AccessTokenName, jwtTokenProvider.generateToken(authentication));
        return  response;
    }

    @GetMapping("/v1/test")
    public WBModel test() {

        return noneDataResponse();
    }

}
