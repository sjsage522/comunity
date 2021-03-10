package com.example.comunity.dto.board;

import com.example.comunity.dto.file.UploadFileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDto extends BoardDto {

    @JsonProperty("board_id")
    private Long boardId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("category_name")
    private String categoryName;

    private String title;
    private String content;

    @JsonProperty("upload_files")
    private List<UploadFileDto> uploadFileDtoList;

    @JsonProperty("created_at")
    private LocalDateTime createdDate;

    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedDate;
}
