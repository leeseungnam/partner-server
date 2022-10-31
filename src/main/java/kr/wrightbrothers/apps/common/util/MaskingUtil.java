package kr.wrightbrothers.apps.common.util;

import org.springframework.util.ObjectUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaskingUtil {

    /**
     * <pre>
     * 이름 마스킹 처리
     *
     * 마스킹 처리 예
     * 홍길동 -> 홍*동
     * 선우용녀 -> 선**녀
     * Chris Martin -> Chr** Mar***
     *
     * </pre>
     * @param name 이름
     * @return 마스킹 이름
     */
    public static String maskingName(String name) {
        // Null OR 한글자 예외 처리
        if (ObjectUtils.isEmpty(name) || name.length() < 2) return name;

        if (name.length() == 2) return name.charAt(0) + "*";

        // 영문 이름 마스킹 처리
        if (Pattern.matches("^[a-zA-Z]*$", name.trim().replaceAll(" ", "")) && name.contains(" ")) {
            // 영문 이름 경우 가운데 공백 으로 인한 스플릿 처리
            String[] names = name.split(" ");

            String firstName = names[0].substring(0, (int) Math.ceil((double) names[0].length()/(double) 2));
            String lastName = names[1].substring(0, (int) Math.ceil((double) names[1].length()/(double) 2));

            return firstName + "*".repeat(names[0].length() - firstName.length()) + " "
                    + lastName + "*".repeat(names[1].length() - lastName.length());
        }

        String target = name.substring(1, name.length() - 1);
        return name.charAt(0) + "*".repeat(target.length()) + name.charAt(name.length() - 1);
    }

    /**
     * <pre>
     * 전화번호 마스킹 처리
     *
     * 마스킹 처리 예
     * 010-1234-5678 -> 010-****-5678
     * 01012345678 -> 010****5678
     * </pre>
     *
     * @param phone 전화번호
     * @return 마스킹 전화번호
     */
    public static String maskingPhone(String phone) {
        String regex = "(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";
        Matcher matcher = Pattern.compile(regex).matcher(phone);

        if (matcher.find()) {
            String target = matcher.group(2);
            return phone.replace(target, "*".repeat(target.length()));
        }

        return phone;
    }

}
