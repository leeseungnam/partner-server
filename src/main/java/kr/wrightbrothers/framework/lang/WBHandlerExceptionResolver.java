package kr.wrightbrothers.framework.lang;

import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBKey.Message;
import kr.wrightbrothers.framework.util.WBResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Slf4j
public class WBHandlerExceptionResolver implements HandlerExceptionResolver{

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//		String token = request.getHeader(WBKey.Jwt.HeaderName);
		ModelAndView mv = new ModelAndView(WBKey.View);
		
		/* 세션이 종료되었을때 발생하는 메세지 */
		if(ex instanceof NullPointerException) {
			if(request.getAttribute(WBKey.JWTExpired) != null && (boolean)request.getAttribute(WBKey.JWTExpired) ) {
				request.removeAttribute(WBKey.JWTExpired);
				ex = new WBBusinessException(0003, Message.Type.Notification);
			}
		}
		
//		new WBResponse().getResponseData(mv, WBKey.Error, token, ex);
		new WBResponse().getResponseData(mv, WBKey.Error, ex);
		return mv;
	}
}
