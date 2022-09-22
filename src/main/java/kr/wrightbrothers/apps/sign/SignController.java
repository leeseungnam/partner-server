package kr.wrightbrothers.apps.sign;

import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.TokenUtil;
import kr.wrightbrothers.apps.sign.dto.SignDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.sign.service.SignService;
import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController()
@RequestMapping(value = "/v1/sign")
@RequiredArgsConstructor
public class SignController extends WBController {

    private final JwtTokenProvider jwtTokenProvider;
    private final SignService signService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final static long REFRESH_TOKEN_VALIDATION_SECOND = 60 * 60 * 2;
    private final UserService userService;

    @PostMapping("/login")
    public WBModel signIn(@RequestBody SignDto paramDto, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(paramDto.getUserId(), paramDto.getUserPwd());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        WBModel wbResponse = new WBModel();

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.issueRefreshToken(authentication);

        log.debug("access token={}",accessToken);
        log.debug("refresh token={}",refreshToken);

        wbResponse.addObject("UserAuth", userService.findAuthById(authentication.getName()));


        response.setHeader(PartnerKey.Jwt.Header.AUTHORIZATION, PartnerKey.Jwt.Type.BEARER + accessToken);
        response.addCookie(TokenUtil.createCookie(PartnerKey.Jwt.Alias.REFRESH_TOKEN, refreshToken, REFRESH_TOKEN_VALIDATION_SECOND));

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

    // [todo] old token 파기
    // [todo] set userAuth validation
    @PostMapping("/auth")
    public WBModel setAuthentic(@RequestHeader(PartnerKey.Jwt.Header.AUTHORIZATION) String accessToken
                                    , @CookieValue(PartnerKey.Jwt.Alias.REFRESH_TOKEN) String refreshToke
                                    , @AuthenticationPrincipal UserPrincipal userPrincipal
                                    , @RequestBody UserAuthDto userAuth
                                    , HttpServletResponse response
                                    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updateAuthorities = new ArrayList<>();
        updateAuthorities.add(new SimpleGrantedAuthority(userAuth.getAuthCode()));

        userPrincipal.changeUserAuth(userAuth);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(userPrincipal, authentication.getCredentials(), updateAuthorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        String newAccessToken = jwtTokenProvider.generateAccessToken(newAuth);
        String newRefreshToken = jwtTokenProvider.issueRefreshToken(authentication);

        log.info("ACCESS_TOKEN[OLD]::{}",accessToken);
        log.info("ACCESS_TOKEN[NEW]::{}",newAccessToken);

        log.info("REFRESH_TOKEN[OLD]::{}",refreshToke);
        log.info("REFRESH_TOKEN[NEW]::{}",newRefreshToken);

        UserPrincipal userDetail = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info(userPrincipal.getUsername());

        if(!ObjectUtils.isEmpty(userPrincipal.getUserAuth())){
            log.info("UserAuth::{},{}",userPrincipal.getUserAuth().getAuthCode(), userPrincipal.getUserAuth().getPartnerCode());
        }else{
            log.info("UserAuth is null");
        }

        response.setHeader(PartnerKey.Jwt.Header.AUTHORIZATION, PartnerKey.Jwt.Type.BEARER + newAccessToken);
        response.addCookie(TokenUtil.createCookie(PartnerKey.Jwt.Alias.REFRESH_TOKEN, newRefreshToken, REFRESH_TOKEN_VALIDATION_SECOND));

        return noneDataResponse();
    }

}
