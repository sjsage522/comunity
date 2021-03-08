package com.example.comunity.controller;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.User;
import com.example.comunity.dto.board.BoardDto;
import com.example.comunity.dto.board.BoardUpdateDto;
import com.example.comunity.dto.board.BoardUploadDto;
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
     */
//    @PostMapping("/boards")
//    public ResponseEntity<EntityModel<BoardDto>> upload(@Valid @RequestBody final BoardUploadDto boardUploadDto, final HttpSession session) {
//        User loginUser = (User) session.getAttribute("authInfo");
//
//        boardService.upload(boardUploadDto, loginUser);
//
//        return ResponseEntity
//                .created(linkTo(methodOn(BoardController.class).findAll()).toUri())
//                .body(assembler.toModel(boardUploadDto));
//    }

//    @PostMapping(value = "/boards")
//    public ResponseEntity<EntityModel<BoardDto>> upload(
//            @RequestPart("boardUploadDto") final String boardUploadString,
//            @RequestPart(value = "files", required = false) final MultipartFile[] files,
//            final HttpSession session) {
//        User loginUser = (User) session.getAttribute("authInfo");
//
//        BoardUploadDto boardUploadDto = boardService.getJson(boardUploadString);
//        boardService.upload(boardUploadDto, loginUser, files);
//
//        return ResponseEntity
//                .created(linkTo(methodOn(BoardController.class).findAll()).toUri())
//                .body(assembler.toModel(boardUploadDto));
//    }

    @PostMapping(value = "/boards")
    public ResponseEntity<EntityModel<BoardDto>> upload(
            @Valid @RequestPart final BoardUploadDto boardUploadDto,
            @RequestPart(value = "files", required = false) final MultipartFile[] files,
            final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        boardService.upload(boardUploadDto, loginUser, files);

        return ResponseEntity
                .created(linkTo(methodOn(BoardController.class).findAll()).toUri())
                .body(assembler.toModel(boardUploadDto));
    }

    /**
     * 게시글 삭제
     *
     * @param id      게시글을 작성한 사용자 id
     * @param name    게시글이 포함된 특정 카테고리 이름
     * @param session 현재 사용자 세션
     */
    @DeleteMapping("/category/{name}/boards/{id}")
    public ResponseEntity<EntityModel<BoardDto>> delete(@PathVariable final Long id, @PathVariable final String name, final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        boardService.delete(id, name, loginUser);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/category/{name}/boards/{id}")
    public ResponseEntity<EntityModel<BoardDto>> update(
            @PathVariable final Long id, @PathVariable final String name, @Valid @RequestBody final BoardUpdateDto boardUpdateDto, final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        boardService.update(id, name, boardUpdateDto, loginUser);

        return ResponseEntity
                .created(linkTo(methodOn(BoardController.class).findByIdWithCategory(id, name)).toUri())
                .body(assembler.toModel(boardUpdateDto));
    }

    /**
     * 특정 카테고리에 포함되는 모든 게시글을 조회
     *
     * @param name 카테고리 명
     */
    @GetMapping("/category/{name}/boards")
    public CollectionModel<EntityModel<BoardDto>> findAllWithCategory(@PathVariable String name) {
        List<EntityModel<BoardDto>> boards = new ArrayList<>();
        for (Board board : boardService.findAllWithCategory(name)) {
            BoardDto boardDto = createBoardDto(board);
            EntityModel<BoardDto> boardDtoEntityModel = assembler.toModel(boardDto);
            boards.add(boardDtoEntityModel);
        }

        return CollectionModel.of(boards,
                linkTo(methodOn(BoardController.class).findAll()).withSelfRel());
    }

    @GetMapping("/boards")
    public ResponseEntity<CollectionModel<EntityModel<BoardDto>>> findAll() {
        List<EntityModel<BoardDto>> boards = new ArrayList<>();
        for (Board board : boardService.findAll()) {
            boards.add(assembler.toModel(createBoardDto(board)));
        }

        return ResponseEntity.ok(CollectionModel.of(boards,
                linkTo(methodOn(BoardController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/category/{name}/boards/{id}")
    public ResponseEntity<EntityModel<BoardDto>> findByIdWithCategory(@PathVariable final Long id, @PathVariable final String name) {
        Board findBoard = boardService.findByIdWithCategory(id, name);

        BoardDto boardDto = createBoardDto(findBoard);

        return ResponseEntity.
                created(linkTo(methodOn(BoardController.class).findAllWithCategory(name)).toUri())
                .body(assembler.toModel(boardDto));
    }

    @Component
    public static class BoardDtoModelAssembler implements RepresentationModelAssembler<BoardDto, EntityModel<BoardDto>> {

        @Override
        public EntityModel<BoardDto> toModel(final BoardDto boardDto) {

            return EntityModel.of(boardDto,
                    linkTo(methodOn(BoardController.class).findByIdWithCategory(boardDto.getBoardId(), boardDto.getCategoryName())).withSelfRel()
                            .andAffordance(afford(methodOn(BoardController.class).update(boardDto.getBoardId(), boardDto.getCategoryName(), null, null)))
                            .andAffordance(afford(methodOn(BoardController.class).delete(boardDto.getBoardId(), boardDto.getCategoryName(), null))),
                    linkTo(methodOn(BoardController.class).findAll()).withRel("boards"));
        }
    }

    private BoardDto createBoardDto(Board board) {
        return new BoardDto(
                board.getBoardId(),
                board.getUser(),
                board.getCategory(),
                board.getTitle(),
                board.getContent(),
                board.getUploadFiles(),
                board.getUser().getUserId(),
                board.getCategory().getName(),
                board.getCreatedDate(),
                board.getLastModifiedDate());
    }
}
