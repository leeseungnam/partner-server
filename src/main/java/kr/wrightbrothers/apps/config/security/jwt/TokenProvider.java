package kr.wrightbrothers.apps.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private final String SECRET_KEY = "aYEFtKMCn0xCg5caH1nnFuHfdAB0lBOvdonxq80VqOGNnG6QcyagXWOLrUdqJnzexUXYceMhGNFNYsA" +
            "6rblSibUEh0yRsJ3XO1um1iMdoekOPzj4zKlokcu9TxTbz5DHYVLkqX3q9JrLgbLZFXD8ynOHfRHRL5Ge64iFZBVm9X517fwZrNornOm" +
            "K2L7hUz10SgZpxAz6";

    private final static String AUTHORITIES_KEY = "auth";
    private final static long TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 2;
    private final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 2;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        return doGenerateToken(authentication, TOKEN_VALIDATION_SECOND);
    }

    public String generateRefreshToken(Authentication authentication) {
        return doGenerateToken(authentication, REFRESH_TOKEN_VALIDATION_SECOND);
    }

    public String doGenerateToken(Authentication authentication,
                                  long expireTime) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + expireTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException | JwtException e) {
            log.info("JWT 토큰이 잘못 되었습니다.");
        }
        return false;
    }

}
