package com.example.comunity.controller.api;

import com.example.comunity.domain.Comment;
import com.example.comunity.domain.User;
import com.example.comunity.dto.api.ApiResult;
import com.example.comunity.dto.comment.CommentApplyRequest;
import com.example.comunity.dto.comment.CommentResponse;
import com.example.comunity.dto.comment.CommentUpdateRequest;
import com.example.comunity.security.Auth;
import com.example.comunity.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.comunity.dto.api.ApiResult.succeed;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 게시글 내 모든 답글 페이지조회
     *
     * @param boardId 게시글 아이디
     * @param page    게시글 페이지 번호
     */
    @GetMapping("/comments/boards/{boardId}")
    public ResponseEntity<ApiResult<List<CommentResponse>>> findAll(
            final @PathVariable Long boardId,
            final @RequestParam @Min(0) int page) {

        final List<CommentResponse> commentResponseList = commentService
                .findAll(boardId, page)
                .stream()
                .map(this::getCommentResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(succeed(commentResponseList));
    }

    /**
     * 특정 게시글에 답글 작성
     *
     * @param commentApplyRequest 답글 작성 dto
     * @param boardId             게시글 아이디
     * @param session             서버 세션
     */
    @Auth
    @PostMapping("/comments/boards/{boardId}")
    public ResponseEntity<ApiResult<CommentResponse>> apply(
            final @Valid @RequestBody CommentApplyRequest commentApplyRequest,
            final @PathVariable Long boardId,
            final HttpSession session) {
        final User loginUser = (User) session.getAttribute("authInfo");
        final Comment newComment = commentService.apply(loginUser, boardId, commentApplyRequest);

        return ResponseEntity
                .created(URI.create("/comments/" + newComment.getId()))
                .body(succeed(getCommentResponseDto(newComment)));
    }

    /**
     * 답글 삭제, 부모 답글 삭제 시 연관된 자식 답글들 모두 삭제
     *
     * @param commentId 답글 아이디
     * @param session   서버 세션
     */
    @Auth
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResult<String>> delete(
            final @PathVariable Long commentId,
            final HttpSession session) {
        final User loginUser = (User) session.getAttribute("authInfo");
        commentService.delete(loginUser, commentId);

        return ResponseEntity
                .ok(succeed("comment is deleted successfully"));
    }

    /**
     * 답글 내용 수정
     *
     * @param commentId        답글 아이디
     * @param commentUpdateDto 답글 수정 dto
     * @param session          서버 세션
     */
    @Auth
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<ApiResult<CommentResponse>> update(
            final @PathVariable Long commentId,
            final @Valid @RequestBody CommentUpdateRequest commentUpdateDto,
            final HttpSession session) {
        final User loginUser = (User) session.getAttribute("authInfo");
        final Comment updateComment = commentService.update(loginUser, commentId, commentUpdateDto);

        return ResponseEntity
                .ok(succeed(getCommentResponseDto(updateComment)));
    }

    /**
     * 계층형 댓글로 나타내기 위한 로직
     */
    private CommentResponse getCommentResponseDto(final Comment comment) {
        final CommentResponse commentResponse = new CommentResponse(comment);

        final Comment parent = comment.getParent();

        // 자식 댓글들은 자신의 부모를 설정
        if (parent != null) commentResponse.setParentId(parent.getId());

        final List<Comment> children = comment.getChildren();

        // 각 부모 댓글에 대한 답글들을 오름차순으로 정렬
        children.sort(Comparator.comparing(Comment::getId));

        for (Comment child : children) {
            final CommentResponse childDto = getCommentResponseDto(child);
            commentResponse.getChildren().add(childDto);
        }
        return commentResponse;
    }
}
