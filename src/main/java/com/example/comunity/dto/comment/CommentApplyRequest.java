package com.example.comunity.dto.comment;

import lombok.*;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;

@Getter
public class CommentApplyRequest {

    private Long parentId;

    @NotBlank(message = "답글 내용을 입력해주세요.")
    private String content;

    private CommentApplyRequest(Long parentId, String content) {
        if (parentId != null && parentId < 0)
            throw new IllegalArgumentException("comment id must not be less than zero");

        this.parentId = parentId;
        this.content = content;
    }

    public static CommentApplyRequest of(Long parentId, String content) {
        Assert.notNull(content, "content must be not null.");
        return new CommentApplyRequest(parentId, content);
    }

    public static CommentApplyRequest from(String content) {
        return new CommentApplyRequest(null, content);
    }
}
