package kr.wrightbrothers.apps.auth;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.auth.dto.AuthEmailDto;
import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.common.util.AwsSesUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.TokenUtil;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Api(tags = {"권한"})
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController extends WBController {
    private final static long REFRESH_TOKEN_VALIDATION_SECOND = 60 * 60 * 2;
    private final AwsSesUtil awsSesUtil;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "인증 이메일 발송", notes = "인증을 위한 이메일을 발송합니다.")
    @PostMapping("/email")
    public WBModel authEmail(@ApiParam @RequestBody AuthEmailDto.ReqBody paramDto) {

        WBModel wbResponse = new WBModel();

        if("1".equals(paramDto.getAuthType())) {
            String authCode = UUID.randomUUID().toString();
            String subject = "라이트브라더스 메일 인증 요청";
            Context context = new Context();
            context.setVariable("code", authCode);
            awsSesUtil.singleSend(paramDto.getUserId(), subject, "userMailAuth.html", context);

            wbResponse.addObject("authEmail", AuthEmailDto.ResBody.builder()
                    .userId(paramDto.getUserId())
                    .authCode(authCode)
                    .build());
        }
        return  wbResponse;
    }

    // [todo] old token 파기
    // [todo] set userAuth validation
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation(value = "인가 정보 변경", notes = "인가(권한) 정보를 변경하기 위한 API 입니다.")
    @PostMapping("/")
    public WBModel setAuthentic(@RequestHeader(PartnerKey.Jwt.Header.AUTHORIZATION) String accessToken
                                    , @CookieValue(PartnerKey.Jwt.Alias.REFRESH_TOKEN) String refreshToke
                                    , @ApiIgnore @AuthenticationPrincipal UserPrincipal userPrincipal
                                    , @ApiParam @RequestBody UserAuthDto userAuth
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
