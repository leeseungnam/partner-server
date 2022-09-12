package kr.wrightbrothers.apps.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FileDto {
    private String fileNo;              // 파일 대표 번호
    private Long fileSeq;               // 파일 번호
    private String fileSource;          // 파일 경로
    private String fileSize;            // 파일 사이즈
    private String fileOriginalName;    // 원본 파일 이름
    private String fileStatus;          // 파일 상태
    private String fileDescription;     // 파일 설명
    private int displaySeq;             // 출력 순서
    private String useFlag;             // 사용 여부
}
