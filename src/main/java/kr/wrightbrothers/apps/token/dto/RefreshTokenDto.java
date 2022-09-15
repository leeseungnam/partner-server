package kr.wrightbrothers.apps.token.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {
    private String userId;       // 코드 값
    private String refreshToken;        // 코드 이름

    public static RefreshTokenDto createToken(String userId, String refreshToken) {
        return new RefreshTokenDto(userId, refreshToken);
    }
    public void changeToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
