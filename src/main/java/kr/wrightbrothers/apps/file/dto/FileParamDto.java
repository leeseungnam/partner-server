package kr.wrightbrothers.apps.file.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileParamDto {
    private String fileNo;      // 파일 대표 번호
    private Long fileSeq;       // 파일 번호
}
