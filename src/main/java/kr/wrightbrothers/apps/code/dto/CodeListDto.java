package kr.wrightbrothers.apps.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CodeListDto {
    /** 코드 값 */
    private String codeValue;

    /** 코드 이름 */
    private String codeName;
}
