package com.example.comunity.dto.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadFileDto {

    private String uploadFileId;
    private Long boardId;
    private String originalFileName;
    private String storedFileName;
    private Long fileSize;
}
