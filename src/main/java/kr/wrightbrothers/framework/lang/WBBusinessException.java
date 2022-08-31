package kr.wrightbrothers.framework.lang;

import kr.wrightbrothers.framework.support.WBKey.Message;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WBBusinessException extends WBException {

	private static final long serialVersionUID = 4597375343779864803L;
	private int errorCode;
	private String type;
	private String[] msgConvert;
	
	public WBBusinessException(int errorCode) {
		init(errorCode, Message.Type.Error, null);
    }
	
	public WBBusinessException(int errorCode, String[] convert) {
		init(errorCode, Message.Type.Error, convert);
    }
	
	public WBBusinessException(int errorCode, String type) {
		init(errorCode, type, null);
    }
	
	public WBBusinessException(int errorCode, String type, String[] convert) {
		init(errorCode, type, convert);
    }
	
	private void init(int errorCode, String type, String[] msgConvert) {
		this.errorCode = errorCode;
		this.type = type;
		this.msgConvert = msgConvert;
	}
	
}
