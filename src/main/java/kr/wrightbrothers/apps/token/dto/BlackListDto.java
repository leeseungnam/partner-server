package kr.wrightbrothers.apps.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
public class BlackListDto {
    private String accessToken;        // 토큰
    private String expireDate;        // 만료일시
}
