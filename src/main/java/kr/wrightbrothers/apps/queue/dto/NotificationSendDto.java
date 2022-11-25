package kr.wrightbrothers.apps.queue.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSendDto {
    private String toPhone;                 // 파트너 코드
    private String templateId;              // 템플릿 아이디
    private String [] templateValue;        // 템플릿 파라미터 값
    private String snsDesc;                 // 알림톡 전송 케이스
}
