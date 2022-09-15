package kr.wrightbrothers.apps.sign;

import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.common.util.CookieUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.sign.dto.SignDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
@Slf4j
@RestController
@RequiredArgsConstructor
public class SignController extends WBController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 2;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final UserService userService;

    @PostMapping("/v1/login")
    public WBModel signIn(@RequestBody SignDto paramDto, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(paramDto.getUserId(), paramDto.getUserPwd());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        WBModel wbResponse = new WBModel();

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.issueRefreshToken(authentication);

        log.debug("access token={}",accessToken);
        log.debug("refresh token={}",refreshToken);

//        wbResponse.addObject(PartnerKey.Jwt.ACCESS_TOKEN, accessToken);
//        wbResponse.addObject(PartnerKey.Jwt.REFRESH_TOKEN, refreshToken);
        wbResponse.addObject("UserAuth", userService.findAuthById(authentication.getName()));


        response.setHeader(AUTHORIZATION_HEADER, "Bearer " + accessToken);
        response.addCookie(CookieUtil.createCookie(PartnerKey.Jwt.REFRESH_TOKEN, refreshToken, REFRESH_TOKEN_VALIDATION_SECOND));
        return  wbResponse;
    }

    @GetMapping("/v1/test")
    public WBModel test(@RequestHeader(AUTHORIZATION_HEADER) String accessToken
                    , @CookieValue(PartnerKey.Jwt.REFRESH_TOKEN) String refreshToken) {

        // verify

        // reissue

        // response set

        return noneDataResponse();
    }
}
