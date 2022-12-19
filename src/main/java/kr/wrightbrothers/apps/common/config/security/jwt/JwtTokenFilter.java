package kr.wrightbrothers.apps.common.config.security.jwt;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.TokenUtil;
import kr.wrightbrothers.apps.sign.service.SignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final SignService signService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 로그 UUID 추가
        MDC.put("thread-id", UUID.randomUUID().toString());
        log.info("[JwtTokenFilter]::doFilterInternal");
        String accessToken = TokenUtil.resolveTokenInHeader(request, PartnerKey.Jwt.Header.AUTHORIZATION);

        if(ObjectUtils.isEmpty(signService.findById(accessToken))){
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken) == PartnerKey.JwtCode.ACCESS) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Security Context {} 인증 정보 저장 완료.", authentication.getName());
            } else if (accessToken != null && jwtTokenProvider.validateToken(accessToken) == PartnerKey.JwtCode.EXPIRED) {
                String refreshToken = TokenUtil.resolveTokenInCookie(request, PartnerKey.Jwt.Alias.REFRESH_TOKEN);

                // 재발급
                if(refreshToken != null && jwtTokenProvider.validateToken(refreshToken) == PartnerKey.JwtCode.ACCESS) {
                    String newRefreshToken = jwtTokenProvider.reissueRefreshToken(accessToken, refreshToken);

                    if(newRefreshToken != null){
//                    response.setHeader(REFRESH_HEADER, "Bearer " + newRefreshToken);
                        response.addHeader("Set-Cookie", jwtTokenProvider.createRefreshTokenResponseCookie(PartnerKey.Jwt.Alias.REFRESH_TOKEN, refreshToken).toString());

                        // access token 생성
                        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                        response.setHeader(PartnerKey.Jwt.Header.AUTHORIZATION, PartnerKey.Jwt.Type.BEARER + jwtTokenProvider.generateAccessToken(authentication));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("reissue refresh Token & access Token");
                    }
                } else {
                    log.info("[JwtTokenFilter]::REFRESH EXPIRED");
                }
            } else {
                log.info("유효한 JWT 토큰이 없습니다.");
            }
        } else {
            log.info("유효하지 않은 JWT 토큰입니다.");
        }
        filterChain.doFilter(request, response);
    }
}
