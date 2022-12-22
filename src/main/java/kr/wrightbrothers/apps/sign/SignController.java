package kr.wrightbrothers.apps.sign;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.TokenUtil;
import kr.wrightbrothers.apps.sign.dto.SignInDto;
import kr.wrightbrothers.apps.user.dto.UserDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(tags = {"인증"})
@Slf4j
@RestController()
@RequestMapping(value = "/v1/sign")
@RequiredArgsConstructor
public class SignController extends WBController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;

    @ApiOperation(value = "로그인", notes = "로그인 요청 API 입니다.")
    @PostMapping("/login")
    public WBModel signIn(@ApiParam @Valid @RequestBody SignInDto paramDto
            , HttpServletRequest request
            , HttpServletResponse response) {
        WBModel wbResponse = new WBModel();

        UserDto user = userService.findUserByDynamic(UserDto.builder().userId(paramDto.getUserId()).build());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(paramDto.getUserId(), paramDto.getUserPwd());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.issueRefreshToken(authentication);

        log.debug("access token={}",accessToken);
        log.debug("refresh token={}",refreshToken);

        response.setHeader(PartnerKey.Jwt.Header.AUTHORIZATION, PartnerKey.Jwt.Type.BEARER + accessToken);
        response.addCookie(jwtTokenProvider.createRefreshTokenCookie(PartnerKey.Jwt.Alias.REFRESH_TOKEN, refreshToken));
//        response.addHeader("Set-Cookie", jwtTokenProvider.createRefreshTokenResponseCookie(PartnerKey.Jwt.Alias.REFRESH_TOKEN, refreshToken).toString());

        user.changePwd(null);

//        wbResponse.addObject(WBKey.WBModel.DefaultDataKey, userService.findAuthById(paramDto.getUserId()));
        wbResponse.addObject(WBKey.WBModel.UserKey, user);

        return  wbResponse;
    }
/*

    @PostMapping("/logout")
    public WBModel signOut(HttpServletRequest request
            , HttpServletResponse response
            , @RequestHeader(PartnerKey.Jwt.Header.AUTHORIZATION) String accessToken
            , @CookieValue(PartnerKey.Jwt.Alias.REFRESH_TOKEN) String refreshToke
            , @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String [] parts = JwtUtil.splitJwt(accessToken);
        JSONObject payload = new JSONObject(JwtUtil.base64Decode(parts[1]));

        Date expireDate =  new Date(payload.getLong("exp") * 1000);

        log.info("[WBLogoutHandler]::logout::blacklist proc");

        // insert blacklist token
        signService.logout(
                BlackListDto.builder()
                        .accessToken(accessToken)
                        .expireDate(expireDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime().toString())
                        .build()
                , RefreshTokenDto.builder()
                        .userId(userPrincipal.getUsername())
                        .refreshToken("")
                        .build()
                );

        // auth clear
        SecurityContext context = SecurityContextHolder.getContext();
        SecurityContextHolder.clearContext();
        context.setAuthentication((Authentication)null);

        //remove token
        response.addCookie(TokenUtil.removeCookie(request, PartnerKey.Jwt.Alias.REFRESH_TOKEN));
        return  noneDataResponse();
    }
*/
    @GetMapping("/logout/response")
    public WBModel signOutSuccess(HttpServletRequest request
            , HttpServletResponse response
    ) {
        return  noneDataResponse();
    }
}
