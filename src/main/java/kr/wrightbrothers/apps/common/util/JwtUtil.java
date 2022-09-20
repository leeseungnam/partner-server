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
public class JwtUtil {

    public static String[] splitJwt(String token) {
        return token.split("\\.");
    }

    public static String base64Decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
}
