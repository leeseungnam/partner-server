package kr.wrightbrothers.apps.user;

import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.common.util.TokenUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.sign.dto.UserDetailDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
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

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController extends WBController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final static long REFRESH_TOKEN_VALIDATION_SECOND = 60 * 60 * 2;
    private final UserService userService;

    @PostMapping("/v1/user")
    public WBModel userJoin(@RequestBody UserDetailDto userDetailDto
            , HttpServletResponse response) {

        WBModel wbResponse = new WBModel();



        return  wbResponse;
    }

    // [todo] old token 파기
    // [todo] set userAuth validation
    @PostMapping("/v1/user/auth")
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
