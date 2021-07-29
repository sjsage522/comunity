package com.example.comunity.dto.comment;

import lombok.*;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;

@Getter
public class CommentUpdateRequest {

    @NotBlank(message = "수정할 답글 내용을 입력해주세요.")
    private String content;

    private CommentUpdateRequest(String content) {
        this.content = content;
    }

    public static CommentUpdateRequest from(String content) {
        Assert.notNull(content, "content must be not null.");
        return new CommentUpdateRequest(content);
    }
}
