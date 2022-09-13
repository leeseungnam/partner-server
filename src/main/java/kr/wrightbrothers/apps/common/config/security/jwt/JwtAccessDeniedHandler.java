package kr.wrightbrothers.apps.common.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.framework.lang.WBGlobalException;
import kr.wrightbrothers.framework.support.WBKey;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        WBGlobalException.exceptionResponse(403, WBKey.Message.Type.Error, null))
        );
    }
}
