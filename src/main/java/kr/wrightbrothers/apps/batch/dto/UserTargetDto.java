package kr.wrightbrothers.apps.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class UserTargetDto {
    private String userName;
    private String receiver;
}
