package kr.wrightbrothers.framework.util;

public class StringUtils {

	/**
	 * 문자열 min, max length 체크
	 */
	public static boolean minmaxlength(int min, int max, String val) {
		if(val.length() < min) return true;
		else return val.length() > max;
	}
}
