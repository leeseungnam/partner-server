package kr.wrightbrothers.apps.common.config.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageDto {
    private String messageNo;           // 에러번호
    private String messageContent;      // 에러내용
    private String messageDescription;  // 에러설명
    private String messageType;         // 에러타입
}
