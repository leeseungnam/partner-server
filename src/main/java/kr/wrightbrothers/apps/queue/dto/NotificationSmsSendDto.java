package kr.wrightbrothers.apps.queue.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSmsSendDto {
    private String toPhone;                 // 파트너 코드
    private String text;                    // 내용
    private String snsDesc;
}
