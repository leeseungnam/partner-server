package kr.wrightbrothers.apps.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends UserAuthDto {
    private String userId;       // 코드 값
    private String userName;        // 코드 이름
}