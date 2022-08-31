package kr.wrightbrothers.framework.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WBMessage {

	@Nullable
	private final Map<String, MessageFormat> messageFormatsPerMessage = new HashMap<>();
	
	public String getMessage(int id, String type) {
		return getMessage(id, type, null);
	}
	
	public String getMessage(int id, String type, String[] bind) {
		String messageStr = "No Search Message....";
		try {
			MessageFormat message = messageFormatsPerMessage.get(type + StringUtils.leftPad(String.valueOf(id), 4, "0"));
			messageStr = message.format(bind);
		}catch (NullPointerException e) {
			log.error(e.getMessage());
		}
		return messageStr;
	}
	
	public void addMessageFormat(String key, MessageFormat messageFormat) {
		this.messageFormatsPerMessage.put(key, messageFormat);
	}
	
}
