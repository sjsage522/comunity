package com.example.comunity.dto.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadFileDto {

    private Long uploadFileId;
    private Long boardId;
    private String originalFileName;
    private String storedFileName;
    private Long fileSize;
    private String fileDownLoadUri;
    private String fileType;

    public UploadFileDto(
            final Long boardId,
            final String originalFileName,
            final String storedFileName,
            final Long fileSize,
            final String fileDownLoadUri,
            final String fileType) {
        this.boardId = boardId;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileSize = fileSize;
        this.fileDownLoadUri = fileDownLoadUri;
        this.fileType = fileType;
    }

    public UploadFileDto(final Long uploadFileId, final String originalFileName, final Long fileSize) {
        this.uploadFileId = uploadFileId;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
    }
}
