package kr.wrightbrothers.apps.token.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BlackListDto {
    private String accessToken;        // 토큰
    private String expireDate;        // 만료일시
}
