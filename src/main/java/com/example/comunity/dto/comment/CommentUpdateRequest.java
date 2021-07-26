package com.example.comunity.dto.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class CommentUpdateRequest {

    private Long parentId;

    @NotBlank(message = "수정할 답글 내용을 입력해주세요.")
    private String content;
}
