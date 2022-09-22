package kr.wrightbrothers.apps.common.config.security;

import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.common.util.JwtUtil;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.TokenUtil;
import kr.wrightbrothers.apps.token.dto.BlackListDto;
import kr.wrightbrothers.apps.token.dto.RefreshTokenDto;
import kr.wrightbrothers.apps.token.service.BlackListService;
import kr.wrightbrothers.apps.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class WBLogoutHandler extends SecurityContextLogoutHandler{

    private final JwtTokenProvider jwtTokenProvider;
    private final BlackListService blackListService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        log.info("[WBLogoutHandler]::logout::START");

        // get token
        String accessToken = TokenUtil.resolveTokenInHeader(request, PartnerKey.Jwt.Header.AUTHORIZATION);

        try {
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken) == PartnerKey.JwtCode.ACCESS) {
                String [] parts = JwtUtil.splitJwt(accessToken);
                JSONObject payload = new JSONObject(JwtUtil.base64Decode(parts[1]));

                Date expireDate =  new Date(payload.getLong("exp") * 1000);
                String sub = payload.getString("sub");
                log.info("[WBLogoutHandler]::logout::sub={}",sub);

                // insert blacklist token
                blackListService.insert(BlackListDto.builder()
                        .accessToken(accessToken)
                        .expireDate(expireDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime().toString())
                        .build());

                // delete refresh token
                refreshTokenService.update(RefreshTokenDto.builder()
                        .userId(sub)
                        .refreshToken("")
                        .build());

                log.info("[WBLogoutHandler]::logout::로그아웃 되었습니다.");
            }else {
                log.info("[WBLogoutHandler]::logout::유효한 토큰이 아닙니다.");
            }
        } catch (Exception e) {
            log.info("[WBLogoutHandler]::logout::Exception");
        }
        log.info("[WBLogoutHandler]::logout::END");
    }
}
