package kr.wrightbrothers.apps.common.config.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kr.wrightbrothers.apps.common.util.JwtUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.TokenUtil;
import kr.wrightbrothers.apps.sign.dto.UserDetailDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.sign.service.WBUserDetailService;
import kr.wrightbrothers.apps.token.dto.RefreshTokenDto;
import kr.wrightbrothers.apps.token.service.RefreshTokenService;
import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    private final String secretKey;
    private final long accessTokenMin;
    private final long refreshTokenMin;
    private final RefreshTokenService refreshTokenService;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
                            @Value("${jwt.validation-time.access-token-min}") long accessTokenMin,
                            @Value("${jwt.validation-time.refresh-token-min}") long refreshTokenMin,
                            RefreshTokenService refreshTokenService) {
        this.secretKey = secretKey;
        this.accessTokenMin = accessTokenMin;
        this.refreshTokenMin = refreshTokenMin;
        this.refreshTokenService = refreshTokenService;
    }
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Authentication authentication) {
        Claims claims = Jwts.claims();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        claims.put(PartnerKey.Jwt.Alias.USER_AUTH, userPrincipal.getUserAuth());
        return createJsonWebToken(authentication, claims, 1000L * 60 * accessTokenMin);
    }

    public String generateRefreshToken(Authentication authentication) {
        Claims claims = Jwts.claims();
        claims.put("value", TokenUtil.createRefreshTokenHash(authentication, 1000L * 60 * refreshTokenMin));
        return createJsonWebToken(authentication, claims, 1000L * 60 * refreshTokenMin);
    }

    public String createJsonWebToken(Authentication authentication, Claims claims, long expireTime) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + expireTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                // auth 삭제 - userAuth 사용 중복
//                .claim(PartnerKey.Jwt.Alias.AUTH, "ROLE".equals(authorities) ? null : authorities)
                .claim(PartnerKey.Jwt.Alias.NAME, userPrincipal.getName())
                .claim(PartnerKey.Jwt.Alias.STATUS, userPrincipal.getUserStatusCode())
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) throws JsonProcessingException {

        String [] parts = JwtUtil.splitJwt(token);
        JSONObject payload = new JSONObject(JwtUtil.base64Decode(parts[1]));

        UserAuthDto userAuthDto = null;

        ObjectMapper mapper = new ObjectMapper();
        userAuthDto = mapper.readValue(payload.get(PartnerKey.Jwt.Alias.USER_AUTH).toString(), UserAuthDto.class);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(userAuthDto.getAuthCode().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserPrincipal principal = new UserPrincipal(UserDetailDto.builder()
                .userId(payload.getString("sub"))
                .userPwd("")
                .userName(payload.getString(PartnerKey.Jwt.Alias.NAME))
                .userStatusCode(payload.getString(PartnerKey.Jwt.Alias.STATUS))
                .userAuth(userAuthDto)
                .build(), authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public PartnerKey.JwtCode validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return PartnerKey.JwtCode.ACCESS;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("[validateToken]::잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("[validateToken]::만료된 JWT 토큰입니다.");
            return PartnerKey.JwtCode.EXPIRED;
        } catch (UnsupportedJwtException e) {
            log.info("[validateToken]::지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException | JwtException e) {
            log.info("[validateToken]::JWT 토큰이 잘못 되었습니다.");
        }
        log.info("[validateToken]::JWT 토큰이 거부 되었습니다.");
        return PartnerKey.JwtCode.DENIED;
    }
    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public String issueRefreshToken(Authentication authentication){
        String newRefreshToken = generateRefreshToken(authentication);

        log.debug("newRefreshToken={}",newRefreshToken);
        RefreshTokenDto refreshTokenDto = refreshTokenService.findById(authentication.getName());

        if(!ObjectUtils.isEmpty(refreshTokenDto)){
            // set newRefreshToken
            refreshTokenDto.changeToken(newRefreshToken);
            refreshTokenService.update(refreshTokenDto);
        }else{
            RefreshTokenDto token = RefreshTokenDto.createToken(authentication.getName(), newRefreshToken);
            refreshTokenService.insert(token);
        }
        return newRefreshToken;
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public String reissueRefreshToken(String accessToken, String refreshToken) throws JsonProcessingException, RuntimeException{
        // check data refresh token
        Authentication authentication = getAuthentication(accessToken);

        RefreshTokenDto findRefreshToken = refreshTokenService.findById(authentication.getName());

        if(ObjectUtils.isEmpty(findRefreshToken)) throw new UsernameNotFoundException("userId : " + authentication.getName() + " was not found");

        if(findRefreshToken.getRefreshToken().equals(refreshToken)){
            //  REFRESH TOKEN 재발급
            String newRefreshToken = generateRefreshToken(authentication);
            findRefreshToken.changeToken(newRefreshToken);
            //  REFRESH TOKEN 갱신처리
            refreshTokenService.update(findRefreshToken);
            
            return newRefreshToken;
        }
        else {
            log.error("refresh 토큰이 일치하지 않습니다. ");
            return null;
        }
    }
}
