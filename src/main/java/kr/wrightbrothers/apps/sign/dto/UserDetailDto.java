package kr.wrightbrothers.apps.sign.dto;

import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {

    private String userId;
    private String userPwd;
    private UserAuthDto userAuth;

}
