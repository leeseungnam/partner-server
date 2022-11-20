package kr.wrightbrothers.apps.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailTargetDto {
    private String name;                // 명칭
    private List<String> receiverList;   // 수신자
}
