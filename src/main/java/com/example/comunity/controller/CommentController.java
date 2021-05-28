package com.example.comunity.controller;

import com.example.comunity.domain.Comment;
import com.example.comunity.domain.User;
import com.example.comunity.dto.comment.CommentApplyRequest;
import com.example.comunity.dto.comment.CommentResponse;
import com.example.comunity.dto.comment.CommentUpdateRequest;
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
    public ResponseEntity<CollectionModel<EntityModel<CommentResponse>>> findAll(
            @PathVariable final Long id, @PathVariable @Min(0) final Integer pageNumber) {
        List<EntityModel<CommentResponse>> responseComments = new ArrayList<>();
        List<Comment> commentList = commentService.findAll(id, pageNumber);

        for (Comment comment : commentList) {
            responseComments.add(EntityModel.of(getCommentResponseDto(comment)));
        }

        return ResponseEntity.ok(CollectionModel.of(responseComments,
                linkTo(methodOn(CommentController.class).findAll(id, pageNumber)).withSelfRel()));
    }

    /**
     * 특정 게시글에 답글 작성
     * @param commentApplyRequest 답글 작성 dto
     * @param id 게시글 번호
     * @param session 현재 사용자 세션
     */
    @PostMapping("/boards/{id}/comments")
    public ResponseEntity<EntityModel<CommentResponse>> apply(
            @Valid @RequestBody final CommentApplyRequest commentApplyRequest,
            @PathVariable final Long id,
            final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        CommentResponse commentResponse = getCommentResponseDto(commentService.apply(loginUser, id, commentApplyRequest));

        return ResponseEntity
                .created(linkTo(methodOn(CommentController.class).apply(commentApplyRequest, id, session)).toUri())
                .body(EntityModel.of(commentResponse,
                        linkTo(methodOn(CommentController.class).apply(commentApplyRequest, id, session)).withSelfRel()));
    }

    /**
     * 답글 삭제, 부모 답글 삭제 시 연관된 자식 답글들 모두 삭제
     * @param id 답글 번호
     * @param session 현재 사용자 세션
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<EntityModel<CommentResponse>> delete(
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
    public ResponseEntity<EntityModel<CommentResponse>> update(
            @PathVariable final Long id,
            @Valid @RequestBody final CommentUpdateRequest commentUpdateDto,
            final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        CommentResponse commentResponse = getCommentResponseDto(commentService.update(loginUser, id, commentUpdateDto));

        return ResponseEntity
                .created(linkTo(methodOn(CommentController.class).update(id, commentUpdateDto, session)).toUri())
                .body(EntityModel.of(commentResponse,
                        linkTo(methodOn(CommentController.class).update(id, commentUpdateDto, session)).withSelfRel()));
    }

    /**
     * 계층형 댓글로 나타내기 위한 로직
     */
    private CommentResponse getCommentResponseDto(Comment comment) {
        CommentResponse commentResponse = new CommentResponse(comment);

        Comment parent = comment.getParent();

        if (parent != null) {
            /**
             * 자식 댓글들은 자신의 부모를 설정
             */
            commentResponse.setParentId(parent.getCommentId());
        }

        List<Comment> children = comment.getChildren();

        /**
         * 각 부모 댓글에 대한 답글들을 오름차순으로 정렬
         */
        children.sort(Comparator.comparing(Comment::getCommentId));

        for (Comment child : children) {
            CommentResponse childDto = getCommentResponseDto(child);
            commentResponse.getChildren().add(childDto);
        }
        return commentResponse;
    }
}
