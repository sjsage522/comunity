package com.example.comunity.controller;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.User;
import com.example.comunity.dto.board.BoardResponse;
import com.example.comunity.dto.board.BoardUpdateRequest;
import com.example.comunity.dto.board.BoardUploadRequest;
import com.example.comunity.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardDtoModelAssembler assembler;

    /**
     * 게시글 작성
     * @param boardUploadRequest 게시글 작성 dto
     * @param files 첨부파일
     * @param session 현재 사용자 세션
     */
    @PostMapping(value = "/boards")
    public ResponseEntity<EntityModel<BoardResponse>> upload(
            @Valid @RequestPart final BoardUploadRequest boardUploadRequest,
            @RequestPart(value = "files", required = false) final MultipartFile[] files,
            final HttpSession session) {

        User loginUser = getCurrentUser(session);
        Board newBoard = boardService.upload(boardUploadRequest, loginUser, files);

        return ResponseEntity
                .created(linkTo(methodOn(BoardController.class).findAll(null)).toUri())
                .body(assembler.toModel(getBoardResponse(newBoard)));
    }

    /**
     * 게시글 삭제
     *
     * @param id      게시글을 작성한 사용자 id
     * @param name    게시글이 포함된 특정 카테고리 이름
     * @param session 현재 사용자 세션
     */
    @DeleteMapping("/category/{name}/boards/{id}")
    public ResponseEntity<EntityModel<BoardResponse>> delete(
            @PathVariable final Long id,
            @PathVariable final String name,
            final HttpSession session) {

        User loginUser = getCurrentUser(session);
        boardService.delete(id, name, loginUser);

        return ResponseEntity.noContent().build();
    }

    /**
     * 게시글 수정
     * @param id 게시글 번호
     * @param name 카테고리 이름
     * @param boardUpdateRequest 게시글 변경 dto
     * @param session 현재 사용자 세션
     */
    @PatchMapping("/category/{name}/boards/{id}")
    public ResponseEntity<EntityModel<BoardResponse>> update(
            @PathVariable final Long id,
            @PathVariable final String name,
            @Valid @RequestBody final BoardUpdateRequest boardUpdateRequest,
            final HttpSession session) {

        User loginUser = getCurrentUser(session);
        Board updatedBoard = boardService.update(id, name, boardUpdateRequest, loginUser);

        return ResponseEntity
                .created(linkTo(methodOn(BoardController.class).findByIdWithCategory(id, name)).toUri())
                .body(assembler.toModel(getBoardResponse(updatedBoard)));
    }

    /**
     * 특정 카테고리에 포함되는 모든 게시글을 조회
     * 페이징 처리 (10개 씩)
     * @param name 카테고리 명
     */
    @GetMapping("/category/{name}/boards/page/{pageNumber}")
    public ResponseEntity<CollectionModel<EntityModel<BoardResponse>>> findAllWithCategory(
            @PathVariable final String name,
            final @PathVariable @Min(0) Integer pageNumber) {

        List<EntityModel<BoardResponse>> boards = new ArrayList<>();
        for (Board board : boardService.findAllWithCategory(name, pageNumber)) {
            boards.add(assembler.toModel(getBoardResponse(board)));
        }

        return ResponseEntity.ok(CollectionModel.of(boards,
                linkTo(methodOn(BoardController.class).findAll(pageNumber)).withSelfRel()));
    }

    /**
     * 모든 게시글 조회 (모든 카테고리내 게시글들 중에서 가장 최근 게시글 10개를 조회)
     * 페이징 처리 (10개 씩)
     */
    @GetMapping("/boards/page/{pageNumber}")
    public ResponseEntity<CollectionModel<EntityModel<BoardResponse>>> findAll(
            @PathVariable @Min(0) final Integer pageNumber) {

        List<EntityModel<BoardResponse>> boards = new ArrayList<>();
        for (Board board : boardService.findAll(pageNumber)) {
            boards.add(assembler.toModel(getBoardResponse(board)));
        }

        return ResponseEntity.ok(CollectionModel.of(boards,
                linkTo(methodOn(BoardController.class).findAll(pageNumber)).withSelfRel()));
    }

    /**
     * 특정 카테고리에 포함된 게시글 단건 조회
     * @param id 게시글 번호
     * @param name 카테고리 이름
     */
    @GetMapping("/category/{name}/boards/{id}")
    public ResponseEntity<EntityModel<BoardResponse>> findByIdWithCategory(
            @PathVariable final Long id,
            @PathVariable final String name) {

        Board findBoard = boardService.findByIdWithCategory(id, name);
        return ResponseEntity.
                created(linkTo(methodOn(BoardController.class).findAllWithCategory(name, null)).toUri())
                .body(assembler.toModel(getBoardResponse(findBoard)));
    }

    /**
     * 리소스 관계 표현
     */
    @Component
    public static class BoardDtoModelAssembler implements RepresentationModelAssembler<BoardResponse, EntityModel<BoardResponse>> {

        @Override
        public EntityModel<BoardResponse> toModel(final BoardResponse boardResponse) {

            return EntityModel.of(boardResponse,
                    linkTo(methodOn(BoardController.class).findByIdWithCategory(boardResponse.getBoardId(), boardResponse.getCategoryName())).withSelfRel()
                            .andAffordance(
                                    afford(methodOn(BoardController.class).update(
                                            boardResponse.getBoardId(), boardResponse.getCategoryName(), null, null)))
                            .andAffordance(
                                    afford(methodOn(BoardController.class).delete(
                                            boardResponse.getBoardId(), boardResponse.getCategoryName(), null))),
                    linkTo(methodOn(BoardController.class).findAll(null)).withRel("boards"));
        }
    }

    /**
     * 응답 dto 생성
     */
    private BoardResponse getBoardResponse(final Board newBoard) {
        return new BoardResponse(newBoard);
    }

    /**
     * 현재 세션에 저장되어 있는 사용자 객체를 반환하는 메서드
     * @param session server session
     * @return currentUser
     */
    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("authInfo");
    }
}
