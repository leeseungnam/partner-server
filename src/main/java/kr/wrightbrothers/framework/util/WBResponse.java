package kr.wrightbrothers.framework.util;

import kr.wrightbrothers.framework.lang.WBBusinessException;
import org.apache.commons.lang3.StringUtils;
import kr.wrightbrothers.framework.support.WBCommon;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBKey.Message;
import org.springframework.web.servlet.ModelAndView;

public class WBResponse {

	public void getResponseData(ModelAndView responseData, String state, Object token, Exception ex) {
		/* Common Data */
		WBCommon common = new WBCommon(state);
		common.setUuid(RandomKey.getUUID());
		if(token != null)
			common.setToken(String.valueOf(token));
		
		/* AccessToken 삭제 */
		responseData.getModel().remove(WBKey.Jwt.AccessTokenName);

		/* Error 발생 시 처리 */
		if(WBKey.Error.endsWith(state)) {
			WBMessage msg = StaticContextAccessor.getBean(WBMessage.class);
			int errorCode = 0;
			String errorType = Message.Type.Error;
			String[] convert = null;
			/* Exception Code 존재 할 때 처리 */
			if(ex.getCause() instanceof WBBusinessException || ex instanceof WBBusinessException) {
				WBBusinessException be = ex!=null?(WBBusinessException) ex : (WBBusinessException) ex.getCause();
				errorCode = be.getErrorCode();
				convert = be.getMsgConvert();
				errorType = be.getType();
			}
			common.setMsgCode(StringUtils.leftPad(String.valueOf(errorCode), 4, "0"));
			common.setMsgType(errorType);
			common.setMessage(msg.getMessage(errorCode, errorType, convert));
		}
		
		responseData.addObject(common);
	}
}
