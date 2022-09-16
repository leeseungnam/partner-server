package kr.wrightbrothers.apps.common.config.security.jwt;

import kr.wrightbrothers.apps.common.util.CookieUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.framework.support.WBKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final static long REFRESH_TOKEN_VALIDATION_SECOND = 60 * 60 * 2;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveTokenInHeader(request, PartnerKey.Jwt.Header.AUTHORIZATION);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken) == PartnerKey.JwtCode.ACCESS) {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Security Context {} 인증 정보 저장 완료.", authentication.getName());
        } else if (accessToken != null && jwtTokenProvider.validateToken(accessToken) == PartnerKey.JwtCode.EXPIRED) {
            String refreshToken = resolveTokenInCookie(request, PartnerKey.Jwt.Alias.REFRESH_TOKEN);

            // 재발급
            if(refreshToken != null && jwtTokenProvider.validateToken(refreshToken) == PartnerKey.JwtCode.ACCESS) {
                String newRefreshToken = jwtTokenProvider.reissueRefreshToken(refreshToken);

                if(newRefreshToken != null){
//                    response.setHeader(REFRESH_HEADER, "Bearer " + newRefreshToken);
                    response.addCookie(CookieUtil.createCookie(PartnerKey.Jwt.Alias.REFRESH_TOKEN, newRefreshToken, REFRESH_TOKEN_VALIDATION_SECOND));

                    // access token 생성
                    Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
                    response.setHeader(PartnerKey.Jwt.Header.AUTHORIZATION, "Bearer " + jwtTokenProvider.generateAccessToken(authentication));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("reissue refresh Token & access Token");
                }
            }
        } else {
            log.info("유효한 JWT 토큰이 없습니다..");
        }
        filterChain.doFilter(request, response);
    }

    private String resolveTokenInHeader(HttpServletRequest request, String name) {
        String token = request.getHeader(name);

        // token check
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    private String resolveTokenInCookie(HttpServletRequest request, String name) {

        String token;

        log.info("[resolveTokenInCookie]::name={}", name);
        try {
            Cookie cookie = CookieUtil.getCookie(request, name);
            token = cookie.getValue();
            log.info("[resolveTokenInCookie]::value={}", token);
        } catch (Exception e) {
            log.error("throw new Exception [resolveTokenInCookie]");
            return null;
        }
        return token;
    }
}
