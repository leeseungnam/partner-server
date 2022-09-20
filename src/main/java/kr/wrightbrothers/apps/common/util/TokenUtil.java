package kr.wrightbrothers.apps.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class TokenUtil {

    // refresh JWT 사용 X
    // base64 encoding hash -> JWT 사용 해야 할 경우 JWT payload에 담아서 사용
    public static String createRefreshTokenHash(Authentication authentication, long expireTime) {

        log.info("[createResfreshToken] method in START");

        String refreshToken = String.format("%s.%s.%s", authentication.getName(), UUID.randomUUID(), expireTime);
        log.info("[createResfreshToken] string format={}",refreshToken);

        refreshToken = Base64.getEncoder().encodeToString(refreshToken.getBytes());
        log.info("[createResfreshToken] base64 encoding={}",refreshToken);

        log.info("[createResfreshToken] method in END");
        return refreshToken;
    }

    public static String resolveTokenInHeader(HttpServletRequest request, String name) {
        String token = request.getHeader(name);

        // token check
        if (token != null && token.startsWith(PartnerKey.Jwt.Type.BEARER)) {
            return token.substring(7);
        }
        return null;
    }

    public static String resolveTokenInCookie(HttpServletRequest request, String name) {

        String token;

        log.info("[resolveTokenInCookie]::name={}", name);
        try {
            Cookie cookie = getCookie(request, name);
            token = cookie.getValue();
            log.info("[resolveTokenInCookie]::value={}", token);
        } catch (Exception e) {
            log.error("throw new Exception [resolveTokenInCookie]");
            return null;
        }
        return token;
    }

    public static Cookie createCookie(String cookieName, String value, long expireTime){
        Cookie cookie = new Cookie(cookieName,value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Long.valueOf(expireTime).intValue());
        cookie.setPath("/");
        return cookie;
    }

    public static Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }
}
