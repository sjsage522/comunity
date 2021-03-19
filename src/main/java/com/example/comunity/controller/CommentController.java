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
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 특정 게시글에 달린 모든 답글들 조회
     * @param id 게시글 번호
     */
    @GetMapping("/boards/{id}/comments/page/{pageNumber}")
    public ResponseEntity<CollectionModel<EntityModel<CommentDto>>> findAll(
            @PathVariable final Long id, @PathVariable @Min(0) final Integer pageNumber) {
        List<EntityModel<CommentDto>> responseComments = new ArrayList<>();
        List<Comment> commentList = commentService.findAll(id, pageNumber);

        for (Comment comment : commentList) {
            responseComments.add(EntityModel.of(getCommentResponseDto(comment)));
        }

        return ResponseEntity.ok(CollectionModel.of(responseComments,
                linkTo(methodOn(CommentController.class).findAll(id, pageNumber)).withSelfRel()));
    }

    /**
     * 특정 게시글에 답글 작성
     * @param commentApplyDto 답글 작성 dto
     * @param id 게시글 번호
     * @param session 현재 사용자 세션
     */
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

    /**
     * 답글 삭제, 부모 답글 삭제 시 연관된 자식 답글들 모두 삭제
     * @param id 답글 번호
     * @param session 현재 사용자 세션
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<EntityModel<CommentDto>> delete(
            @PathVariable final Long id,
            final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        commentService.delete(loginUser, id);

        return ResponseEntity.noContent().build();
    }

    /**
     * 답글 내용 수정
     * @param id 답글 번호
     * @param commentUpdateDto 답글 수정 dto
     * @param session 현재 사용자 세션
     */
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
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);

        Comment parent = comment.getParent();

        if (parent != null) {
            commentResponseDto.setParentId(parent.getCommentId());
        }

        List<Comment> children = comment.getChildren();
        children.sort(Comparator.comparing(Comment::getCommentId));

        for (Comment child : children) {
            CommentResponseDto childDto = getCommentResponseDto(child);
            commentResponseDto.getChildren().add(childDto);
        }
        return commentResponseDto;
    }
}
