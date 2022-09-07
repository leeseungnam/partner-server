package kr.wrightbrothers.apps.common.util;

import kr.wrightbrothers.framework.support.WBKey;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class RandomUtil {

    private final static int charA = 65;
    private final static int charZ = 90;
    private final static int char0 = 48;
    private final static int char9 = 57;

    // UUID 조회
    public static String getUUID() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return String.valueOf(request.getAttribute(WBKey.UUID));
    }

    // UUID 생성
    public static void setUUID() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        request.setAttribute(WBKey.UUID, generateNo());
    }

    // 랜덤번호 생성
    public static String generateNo() {
        return new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date()) + RandomUtils.nextLong();
    }

    // 랜덤문자 생성
    public static String generateChar(int length) {
        Random random = new Random();
        return random.ints(charA, charZ + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    // 랜덤코드 생성
    public static String generateCode(int length) {
        Random random = new Random();
        return random.ints(char0, charZ + 1)
                .filter(_char -> _char >= char0)
                .filter(_char -> _char <= charZ)
                .filter(_char -> _char <= char9 | _char >= charA)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
