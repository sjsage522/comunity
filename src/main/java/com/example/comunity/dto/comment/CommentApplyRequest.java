package com.example.comunity.dto.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class CommentApplyRequest {

    private Long parentId;

    @NotBlank(message = "답글 내용을 입력해주세요.")
    private String content;
}
