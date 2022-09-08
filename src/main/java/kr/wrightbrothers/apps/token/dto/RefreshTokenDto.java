package kr.wrightbrothers.apps.token.dto;

import lombok.Data;

@Data
public class RefreshTokenDto {
    private String userId;       // 코드 값
    private String token;        // 코드 이름

    private RefreshTokenDto(String id, String token) {
        this.userId = id;
        this.token = token;
    }

    public static RefreshTokenDto createToken(String userId, String token) {
        return new RefreshTokenDto(userId, token);
    }

    public void changeToken(String token) {
        this.token = token;
    }
}
