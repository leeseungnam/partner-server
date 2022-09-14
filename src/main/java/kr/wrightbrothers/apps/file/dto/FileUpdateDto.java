package kr.wrightbrothers.apps.file.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

@Getter
@Jacksonized
@SuperBuilder
public class FileUpdateDto extends FileUploadDto {
    @JsonIgnore
    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileName() {
        if (!ObjectUtils.isEmpty(getFileSource()) & getFileSource().contains("/"))
            return getFileSource().split("/")[getFileSource().split("/").length -1];

        return null;
    }
}
