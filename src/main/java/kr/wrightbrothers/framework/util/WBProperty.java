package kr.wrightbrothers.framework.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class WBProperty {

	@Autowired
	private Environment env;
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return env.getProperty(key);
	}

}
