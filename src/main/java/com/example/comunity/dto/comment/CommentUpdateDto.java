package com.example.comunity.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateDto extends CommentDto {

    private Long parentId;

    @NotBlank(message = "수정할 답글 내용을 입력해주세요.")
    private String content;
}
