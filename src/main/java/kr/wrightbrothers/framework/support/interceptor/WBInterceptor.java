package kr.wrightbrothers.framework.support.interceptor;

import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.util.WBResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WBInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        if (request.getMethod().equals(WBKey.RequestMethodOption)) return;
        if (ObjectUtils.isEmpty(modelAndView)) return;

        // 로그인 생성 토큰
//        Object token = modelAndView.getModel().get(WBKey.Jwt.AccessTokenName);
        // 최종 ResponseData 처리
//        new WBResponse().getResponseData(modelAndView, WBKey.Success, token, null);
        new WBResponse().getResponseData(modelAndView, WBKey.Success, null);
    }
}
