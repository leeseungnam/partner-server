package kr.wrightbrothers.apps.sign.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String usrId;
    private String usrPw;
    private List<AuthorityDto> authorities;

}
