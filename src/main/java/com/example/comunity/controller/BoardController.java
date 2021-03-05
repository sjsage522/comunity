package com.example.comunity.controller;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.User;
import com.example.comunity.dto.board.BoardDto;
import com.example.comunity.dto.board.BoardUploadDto;
import com.example.comunity.dto.user.UserDto;
import com.example.comunity.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardDtoModelAssembler assembler;

    @PostMapping("/boards")
    public ResponseEntity<EntityModel<BoardDto>> upload(@Valid @RequestBody final BoardUploadDto boardUploadDto, final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        boardUploadDto.setUser(loginUser);
        boardUploadDto.setUserId(loginUser.getUserId());
        boardService.upload(boardUploadDto);

        return ResponseEntity
                .created(linkTo(methodOn(BoardController.class).findAll()).toUri())
                .body(EntityModel.of(boardUploadDto,
                        linkTo(methodOn(BoardController.class).upload(boardUploadDto, session)).withSelfRel(),
                        linkTo(methodOn(BoardController.class).findAll()).withRel("boards")));
    }

    /**
     * 특정 카테고리에 포함되는 모든 게시글을 조회
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
    public CollectionModel<EntityModel<BoardDto>> findAll() {
        List<EntityModel<BoardDto>> boards = new ArrayList<>();
        for (Board board : boardService.findAll()) {
            BoardDto boardDto = createBoardDto(board);
            EntityModel<BoardDto> boardDtoEntityModel = assembler.toModel(boardDto);
            boards.add(boardDtoEntityModel);
        }

        return CollectionModel.of(boards,
                linkTo(methodOn(BoardController.class).findAll()).withSelfRel());
    }

//    @GetMapping("/category/{name}/board/{id}")
//    public ResponseEntity<EntityModel<BoardDto>> findByIdWithCategory(@PathVariable final Long id, @PathVariable final String name) {
//        Board findBoard = boardService.findByIdWithCategory(id, name);
//
//        BoardDto boardDto = createBoardDto(findBoard);
//
//    }

    private BoardDto createBoardDto(Board board) {
        return new BoardDto(
                board.getBoardId(),
                board.getUser(),
                board.getCategory(),
                board.getTitle(),
                board.getContent(),
                board.getBoardUri(),
                board.getUploadFiles());
    }

    @Component
    public static class BoardDtoModelAssembler implements RepresentationModelAssembler<BoardDto, EntityModel<BoardDto>> {

        @Override
        public EntityModel<BoardDto> toModel(final BoardDto boardDto) {

            return EntityModel.of(boardDto,
                    linkTo(methodOn(UserController.class).findAll()).withRel("boards"));
        }
    }
}
