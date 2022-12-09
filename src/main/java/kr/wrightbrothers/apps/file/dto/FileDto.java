package kr.wrightbrothers.apps.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
public class FileDto {
    /** 파일 대표 번호 */
    private String fileNo;

    /** 파일 번호 */
    private Long fileSeq;

    /** 파일 경로 */
    private String fileSource;

    /** 파일 사이즈 */
    private String fileSize;

    /** 원본 파일 이름 */
    private String fileOriginalName;

    /** 파일 상태 */
    private String fileStatus;

    /** 파일 설명 */
    private String fileDescription;

    /** 출력 순서 */
    private int displaySeq;

    /** 사용 여부 */
    private String useFlag;
}
