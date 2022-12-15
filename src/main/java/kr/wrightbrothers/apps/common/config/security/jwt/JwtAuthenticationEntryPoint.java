package kr.wrightbrothers.apps.common.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.TokenUtil;
import kr.wrightbrothers.framework.lang.WBGlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String accessToken = TokenUtil.resolveTokenInHeader(request, PartnerKey.Jwt.Header.AUTHORIZATION);
        String refreshToken = TokenUtil.resolveTokenInCookie(request, PartnerKey.Jwt.Alias.REFRESH_TOKEN);

        if(!ObjectUtils.isEmpty(accessToken)) {
            if (ObjectUtils.isEmpty(refreshToken)) {
                response.getWriter()
                        .write(new ObjectMapper()
                                .writeValueAsString(WBGlobalException.exceptionResponse(ErrorCode.UNAUTHORIZED_TOKEN.getErrCode(), "E", null)));
            } else {
                response.getWriter()
                        .write(new ObjectMapper()
                                .writeValueAsString(WBGlobalException.exceptionResponse(ErrorCode.FORBIDDEN_LOGOUT.getErrCode(), "E", null)));
            }
        } else {
            response.getWriter()
                    .write(new ObjectMapper()
                            .writeValueAsString(WBGlobalException.exceptionResponse(ErrorCode.FORBIDDEN_LOGIN.getErrCode(), "E", null)));
        }
    }
}
