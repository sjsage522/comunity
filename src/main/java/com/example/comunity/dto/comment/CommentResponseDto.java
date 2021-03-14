package com.example.comunity.dto.comment;

import com.example.comunity.domain.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto extends CommentDto {

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("comment_id")
    private Long commentId;

    private String content;

    @JsonProperty("writer")
    private String userId;

    @JsonProperty("board_id")
    private Long boardId;

    private final List<CommentResponseDto> children = new ArrayList<>();

    @JsonProperty("created_at")
    private LocalDateTime createdDate;
    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedDate;

    public CommentResponseDto(final Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.userId = comment.getUser().getUserId();
        this.boardId = comment.getBoard().getBoardId();
        this.createdDate = comment.getCreatedDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
    }
}
