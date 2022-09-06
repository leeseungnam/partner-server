package kr.wrightbrothers.apps.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CodeListDto {
    private String codeValue;       // 코드 값
    private String codeName;        // 코드 이름
}
