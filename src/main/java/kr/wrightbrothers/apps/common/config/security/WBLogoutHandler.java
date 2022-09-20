package kr.wrightbrothers.apps.common.config.security;

import io.jsonwebtoken.Jwts;
import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.common.util.JwtUtil;
import kr.wrightbrothers.apps.common.util.TokenUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.token.dto.BlackListDto;
import kr.wrightbrothers.apps.token.dto.RefreshTokenDto;
import kr.wrightbrothers.apps.token.service.BlackListService;
import kr.wrightbrothers.apps.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class WBLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final BlackListService blackListService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        log.info("[WBLogoutHandler]::logout::START");

        // get token
        String accessToken = TokenUtil.resolveTokenInHeader(request, PartnerKey.Jwt.Header.AUTHORIZATION);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken) == PartnerKey.JwtCode.ACCESS) {
            String [] parts = JwtUtil.splitJwt(accessToken);
            JSONObject payload = new JSONObject(JwtUtil.base64Decode(parts[1]));

            log.info("[WBLogoutHandler]::logout::exp={}, cur={}",payload.getLong("exp"), System.currentTimeMillis() / 1000);

            log.info("[WBLogoutHandler]::logout::blacklist proc");
            // insert blacklist token
            blackListService.insert(BlackListDto.builder()
                    .accessToken(accessToken)
                    .expireDate("")
                    .build());

            // delete refresh token
            refreshTokenService.update(RefreshTokenDto.builder()
                    .userId(authentication.getName())
                    .refreshToken("")
                    .build());

            log.info("[WBLogoutHandler]::logout::로그아웃 되었습니다.");
        }else {
            log.info("[WBLogoutHandler]::logout::token fail");
        }
        log.info("[WBLogoutHandler]::logout::END");
    }
}
