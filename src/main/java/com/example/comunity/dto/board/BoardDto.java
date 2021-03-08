package com.example.comunity.dto.board;

import com.example.comunity.domain.Category;
import com.example.comunity.domain.UploadFile;
import com.example.comunity.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardDto {

    private Long boardId;

    @JsonIgnore
    private User user;

    @JsonIgnore
    private Category category;

    private String title;
    private String content;

    private String boardUri;

    private List<UploadFile> uploadFiles;

    @JsonProperty("writer")
    private String userId;
    private String categoryName;

    @JsonProperty("created_at")
    private LocalDateTime createdDate;
    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedDate;
}
