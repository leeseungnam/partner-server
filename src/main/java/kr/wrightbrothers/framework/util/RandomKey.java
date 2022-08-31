package kr.wrightbrothers.framework.util;

import kr.wrightbrothers.framework.support.WBKey;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RandomKey {

    private static String ENGLISH_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static String ENGLISH_UPPER = ENGLISH_LOWER.toUpperCase();
    private static String NUMBER = "0123456789";
    private static String DATA_FOR_RANDOM_STRING = ENGLISH_UPPER + NUMBER;
    private static int random_string_length = 10;
	private static SecureRandom random = new SecureRandom();
    
	public static String getUUID() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return (String) req.getAttribute(WBKey.UUID);
	}

	public static String setUUID() {
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		Date currentDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
		String uuid = simpleDateFormat.format(currentDate) + RandomUtils.nextLong();
		req.setAttribute(WBKey.UUID, uuid);
		return uuid;
	}
	
	public static String getFileKey() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
		return simpleDateFormat.format(new Date()) + RandomUtils.nextLong();
	}

	/** 랜덤 문자열을 생성한다 **/
	public static String generate(int randomNumber) {
		StringBuilder sb = new StringBuilder(randomNumber);
		for (int i = 0; i < randomNumber; i++) {
			sb.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
		}
		return sb.toString();
	}

	/** 숫자 포함 문자열을 생성한다 **/
	public static String generateCharNumber(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(DATA_FOR_RANDOM_STRING.charAt(new SecureRandom().nextInt(DATA_FOR_RANDOM_STRING.length())));
		}
		return sb.toString();
	}

	/** 생성날짜 기준으로 생성한다. **/
	public static String getUniqueId() {
		return new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
	}
	
	/***
	 * SQS 파일 생성 키
	 */
	public static String getFileKey(String key, String index) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
		String uuid = simpleDateFormat.format(new Date()) + RandomUtils.nextLong();
		return key + uuid;
	}
}
