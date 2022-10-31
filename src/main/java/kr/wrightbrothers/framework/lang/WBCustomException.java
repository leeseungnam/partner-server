package kr.wrightbrothers.framework.lang;

import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.framework.support.WBKey.Message;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WBCustomException extends WBException {

	private static final long serialVersionUID = 4597375343779864803L;
	private ErrorCode errorCode;
	private String messageId;
	private Object [] messageArgs;

	public WBCustomException(String messageId) {
		init(ErrorCode.INTERNAL_SERVER, messageId, null);
    }
	public WBCustomException(String messageId, Object [] messageArgs) {
		init(ErrorCode.INTERNAL_SERVER, messageId, messageArgs);
	}
	public WBCustomException(ErrorCode errorCode, String messageId, Object [] messageArgs) {
		init(errorCode, messageId, messageArgs);
    }

	private void init(ErrorCode errorCode, String messageId, Object [] messageArgs) {
		this.errorCode = errorCode;
		this.messageId = messageId;
		this.messageArgs = messageArgs;
	}
	
}
