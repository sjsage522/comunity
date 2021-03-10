package com.example.comunity.controller;

import com.example.comunity.domain.Comment;
import com.example.comunity.domain.User;
import com.example.comunity.dto.comment.CommentApplyDto;
import com.example.comunity.dto.comment.CommentDto;
import com.example.comunity.dto.comment.CommentResponseDto;
import com.example.comunity.dto.comment.CommentUpdateDto;
import com.example.comunity.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/boards/{id}/comments")
    public ResponseEntity<CollectionModel<EntityModel<CommentDto>>> findAll(@PathVariable final Long id) {
        List<EntityModel<CommentDto>> responseComments = new ArrayList<>();
        List<Comment> commentList = commentService.findAll(id);

        for (Comment comment : commentList) {
            CommentResponseDto commentResponseDto = getCommentResponseDto(comment);
            if (commentResponseDto.getParentId() == null)
                responseComments.add(EntityModel.of(commentResponseDto));
        }

        return ResponseEntity.ok(CollectionModel.of(responseComments,
                linkTo(methodOn(CommentController.class).findAll(id)).withSelfRel()));
    }

    @PostMapping("/boards/{id}/comments")
    public ResponseEntity<EntityModel<CommentDto>> apply(
            @Valid @RequestBody final CommentApplyDto commentApplyDto,
            @PathVariable final Long id,
            final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        CommentResponseDto commentResponseDto = getCommentResponseDto(commentService.apply(loginUser, id, commentApplyDto));

        return ResponseEntity
                .created(linkTo(methodOn(CommentController.class).apply(commentApplyDto, id, session)).toUri())
                .body(EntityModel.of(commentResponseDto,
                        linkTo(methodOn(CommentController.class).apply(commentApplyDto, id, session)).withSelfRel()));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<EntityModel<CommentDto>> delete(
            @PathVariable final Long id,
            final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        commentService.delete(loginUser, id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<EntityModel<CommentDto>> update(
            @PathVariable final Long id,
            @Valid @RequestBody final CommentUpdateDto commentUpdateDto,
            final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        CommentResponseDto commentResponseDto = getCommentResponseDto(commentService.update(loginUser, id, commentUpdateDto));

        return ResponseEntity
                .created(linkTo(methodOn(CommentController.class).update(id, commentUpdateDto, session)).toUri())
                .body(EntityModel.of(commentResponseDto,
                        linkTo(methodOn(CommentController.class).update(id, commentUpdateDto, session)).withSelfRel()));
    }

    /**
     * 계층형 댓글로 나타내기 위한 로직
     */
    private CommentResponseDto getCommentResponseDto(Comment comment) {
        CommentResponseDto commentResponseDto = new CommentResponseDto(
                comment.getCommentId(),
                comment.getContent(),
                comment.getUser().getUserId(),
                comment.getBoard().getBoardId(),
                comment.getCreatedDate(),
                comment.getLastModifiedDate()
        );

        Comment parent = comment.getParent();

        if (parent != null) {
            commentResponseDto.setParentId(parent.getCommentId());
        }

        List<Comment> children = comment.getChildren();

        for (Comment child : children) {
            CommentResponseDto childDto = getCommentResponseDto(child);
            commentResponseDto.getChildren().add(childDto);
        }
        return commentResponseDto;
    }
}
