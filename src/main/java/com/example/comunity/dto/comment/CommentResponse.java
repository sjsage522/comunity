package com.example.comunity.dto.comment;

import com.example.comunity.domain.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"parentId", "commentId", "boardId", "userId", "createdDate", "lastModifiedDate", "content", "children"})
public class CommentResponse {

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("comment_id")
    private Long commentId;

    private String content;

    @JsonProperty("writer")
    private String userId;

    @JsonProperty("board_id")
    private Long boardId;

    private final List<CommentResponse> children = new ArrayList<>();

    @JsonProperty("created_at")
    private LocalDateTime createdDate;
    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedDate;

    public CommentResponse(final Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.userId = comment.getUser().getUserId();
        this.boardId = comment.getBoard().getBoardId();
        this.createdDate = comment.getCreatedDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
    }
}
