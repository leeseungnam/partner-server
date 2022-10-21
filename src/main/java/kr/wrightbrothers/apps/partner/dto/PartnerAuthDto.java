package kr.wrightbrothers.apps.partner.dto;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerAuthDto {
    private String partnerCode;     // 파트너 코드
    private String userId;     // 유저 아이디
    private String authCode;     // 권한 코드

    public void changeUserId (String userId) {
        this.userId = userId;
    }
}
