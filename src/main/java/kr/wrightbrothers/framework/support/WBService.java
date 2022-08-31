package kr.wrightbrothers.framework.support;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WBService {

	/**
	 * 오류 로그 설정
	 * @param e
	 */
	public void LogStackTrace(Throwable e) {
		log.error("", e);
	}
}
