package com.example.comunity.controller;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.User;
import com.example.comunity.dto.api.ApiResult;
import com.example.comunity.dto.board.BoardResponse;
import com.example.comunity.dto.board.BoardUpdateRequest;
import com.example.comunity.dto.board.BoardUploadRequest;
import com.example.comunity.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.comunity.dto.api.ApiResult.succeed;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 작성
     *
     * @param boardUploadRequest 게시글 작성 dto
     * @param files              첨부파일
     * @param session            서버 세션
     */
    @PostMapping("/boards")
    public ResponseEntity<ApiResult<BoardResponse>> uploadBoard(
            final @Valid @ModelAttribute BoardUploadRequest boardUploadRequest,
            final @RequestPart(value = "files", required = false) MultipartFile[] files,
            final HttpSession session) {
        log.info("request = {}", boardUploadRequest);

        final User loginUser = getCurrentUser(session);
        final Board newBoard = boardService.upload(boardUploadRequest, loginUser, files);

        return ResponseEntity
                .created(URI.create("/boards/" + newBoard.getId()))
                .body(succeed(getBoardResponse(newBoard)));
    }

    /**
     * 게시글 삭제
     *
     * @param boardId      게시글을 작성한 사용자 아이디
     * @param categoryName 게시글이 포함된 특정 카테고리 이름
     * @param session      서버 세션
     */
    @DeleteMapping("/boards/{boardId}/category/{categoryName}")
    public ResponseEntity<ApiResult<String>> deleteBoard(
            final @PathVariable Long boardId,
            final @PathVariable String categoryName,
            final HttpSession session) {

        final User loginUser = getCurrentUser(session);
        boardService.delete(boardId, categoryName, loginUser);

        return ResponseEntity
                .ok(succeed("board is deleted successfully"));
    }

    /**
     * 게시글 수정
     *
     * @param boardId            게시글 번호
     * @param categoryName       카테고리 이름
     * @param boardUpdateRequest 게시글 변경 dto
     * @param session            서버 세션
     */
    @PatchMapping("/boards/{boardId}/category/{categoryName}")
    public ResponseEntity<ApiResult<BoardResponse>> updateBoard(
            final @PathVariable Long boardId,
            final @PathVariable String categoryName,
            final @Valid @RequestBody BoardUpdateRequest boardUpdateRequest,
            final HttpSession session) {

        final User loginUser = getCurrentUser(session);
        final Board updatedBoard = boardService.update(boardId, categoryName, boardUpdateRequest, loginUser);

        return ResponseEntity
                .ok(succeed(getBoardResponse(updatedBoard)));
    }

    /**
     * 특정 카테고리에 포함되는 모든 게시글을 조회
     * 페이징 처리 (10개 씩)
     *
     * @param categoryName 카테고리 이름
     * @param page         게시글 페이지 번호
     */
    @GetMapping("/boards/category/{categoryName}")
    public ResponseEntity<ApiResult<List<BoardResponse>>> findAllWithCategory(
            final @PathVariable String categoryName,
            final @RequestParam @Min(0) int page) {

        List<BoardResponse> boardResponseList = boardService
                .findAllWithCategory(categoryName, page)
                .stream()
                .map(this::getBoardResponse)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(succeed(boardResponseList));
    }

    /**
     * 모든 게시글 조회 (모든 카테고리내 게시글들 중에서 가장 최근 게시글 10개를 조회)
     * 페이징 처리 (10개 씩)
     *
     * @param page 게시글 페이지 번호
     */
    @GetMapping("/boards")
    public ResponseEntity<ApiResult<List<BoardResponse>>> findAll(
            final @RequestParam @Min(0) int page) {

        List<BoardResponse> boardResponseList = boardService
                .findAll(page)
                .stream()
                .map(this::getBoardResponse)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(succeed(boardResponseList));
    }

    /**
     * 특정 카테고리에 포함된 게시글 단건 조회
     *
     * @param boardId      게시글 아이디
     * @param categoryName 카테고리 이름
     */
    @GetMapping("/boards/{boardId}/category/{categoryName}")
    public ResponseEntity<ApiResult<BoardResponse>> findByIdWithCategory(
            final @PathVariable Long boardId,
            final @PathVariable String categoryName) {
        final Board findBoard = boardService.findByIdWithCategory(boardId, categoryName);

        return ResponseEntity
                .ok(succeed(getBoardResponse(findBoard)));
    }

    /**
     * 게시글 단건 조회
     *
     * @param boardId 조회할 게시글 아이디
     */
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<ApiResult<BoardResponse>> findById(
            final @PathVariable Long boardId) {
        final Board findBoard = boardService.findById(boardId);

        return ResponseEntity
                .ok(succeed(getBoardResponse(findBoard)));
    }

    /**
     * 응답 dto 생성
     */
    private BoardResponse getBoardResponse(final Board newBoard) {
        return new BoardResponse(newBoard);
    }

    /**
     * 현재 세션에 저장되어 있는 사용자 객체를 반환하는 메서드
     *
     * @param session server session
     * @return currentUser
     */
    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("authInfo");
    }
}
