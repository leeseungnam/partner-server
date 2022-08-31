package kr.wrightbrothers.framework.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.wrightbrothers.framework.support.WBKey;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtToken {

    private final String SECRET_KEY = "aYEFtKMCn0xCg5caH1nnFuHfdAB0lBOvdonxq80VqOGNnG6QcyagXWOLrUdqJnzexUXYceMhGNFNYsA" +
            "6rblSibUEh0yRsJ3XO1um1iMdoekOPzj4zKlokcu9TxTbz5DHYVLkqX3q9JrLgbLZFXD8ynOHfRHRL5Ge64iFZBVm9X517fwZrNornOm" +
            "K2L7hUz10SgZpxAz6";
    
//    public final static long TOKEN_VALIDATION_SECOND = 1000L * 60 * 1;
//    public final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 1;

    public final static long TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 2;
    public final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 2;
    
    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsername(String token) {
        return extractAllClaims(token).get(WBKey.Jwt.TokenKey, String.class);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public String generateToken(String username) {
        return doGenerateToken(username, TOKEN_VALIDATION_SECOND);
    }

    public String generateRefreshToken(String username) {
        return doGenerateToken(username, REFRESH_TOKEN_VALIDATION_SECOND);
    }

    public String doGenerateToken(String username, long expireTime) {
    	Claims claims = Jwts.claims();
        claims.put(WBKey.Jwt.TokenKey, username);

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }

    public Boolean validateToken(String token, String userId) {
        final String username = getUsername(token);
        return (username.equals(userId) && !isTokenExpired(token));
    }

}
