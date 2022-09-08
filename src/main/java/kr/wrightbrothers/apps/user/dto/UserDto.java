package kr.wrightbrothers.apps.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private String userId;       // 코드 값
    private String userName;        // 코드 이름
}
