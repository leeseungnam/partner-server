package kr.wrightbrothers.apps.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Component
public class TokenUtil {
    private static boolean httpOnly;
    private static boolean secure;
    private static String sameSite;

    public TokenUtil(@Value("${app.cookie.sameSite}") String sameSite,
                            @Value("${app.cookie.secure}") boolean secure,
                            @Value("${app.cookie.httpOnly}") boolean httpOnly
    ) {
        this.httpOnly = httpOnly;
        this.secure = secure;
        this.sameSite = sameSite;
    }

    // refresh JWT 사용 X
    // base64 encoding hash -> JWT 사용 해야 할 경우 JWT payload에 담아서 사용
    public static String createRefreshTokenHash(Authentication authentication, long expireTime) {

        String refreshToken = String.format("%s.%s.%s", authentication.getName(), UUID.randomUUID(), expireTime);
        refreshToken = Base64.getEncoder().encodeToString(refreshToken.getBytes());

        return refreshToken;
    }

    public static String resolveTokenInHeader(HttpServletRequest request, String name) {

        String token = request.getHeader(name);

        // token check
        if (token != null && token.startsWith(PartnerKey.Jwt.Type.BEARER)) {
            return token.split(PartnerKey.Jwt.Type.BEARER)[1].trim();
        }
        return null;
    }

    public static String resolveTokenInCookie(HttpServletRequest request, String name) {

        String token;

        try {
            Cookie cookie = getCookie(request, name);
            token = cookie.getValue();

        } catch (Exception e) {
            log.error("throw new Exception [resolveTokenInCookie]");
            return null;
        }
        return token;
    }

    public static ResponseCookie createResponseCookie(String cookieName, String value, long expireTime){
        return ResponseCookie.from(cookieName, value)
                .sameSite(sameSite)
                .secure(secure)
                .httpOnly(httpOnly)
                .path("/")
                .maxAge(Long.valueOf(expireTime).intValue())
                .build();
    }
    public static Cookie createCookie(String cookieName, String value, long expireTime){
        Cookie cookie = new Cookie(cookieName,value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Long.valueOf(expireTime).intValue());
        cookie.setPath("/");
        return cookie;
    }

    public static Cookie getCookie(HttpServletRequest request, String cookieName){
        final Cookie[] cookies = request.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }

    public static Cookie removeCookie(HttpServletRequest request, String cookieName) {
        Cookie cookie = new Cookie(cookieName, (String)null);
        String contextPath = request.getContextPath();
        String cookiePath = StringUtils.hasText(contextPath) ? contextPath : "/";
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        cookie.setSecure(request.isSecure());

        return cookie;
    }
}
