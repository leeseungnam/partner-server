package kr.wrightbrothers.apps.file.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileListDto {
    @ApiModelProperty(value = "파일 대표 번호")
    private String fileNo;
    @ApiModelProperty(value = "파일 순번")
    private Long fileSeq;
    @ApiModelProperty(value = "파일 경로")
    private String fileSource;
    @ApiModelProperty(value = "파일 사이즈")
    private String fileSize;
    @ApiModelProperty(value = "원본 파일 이름")
    private String fileOriginalName;
    @ApiModelProperty(value = "파일 상태")
    private String fileStatus;
    @ApiModelProperty(value = "파일 설명")
    private String fileDescription;
    @ApiModelProperty(value = "디스플레이 순서")
    private int displaySeq;
}