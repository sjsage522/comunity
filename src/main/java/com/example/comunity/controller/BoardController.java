package com.example.comunity.controller;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.User;
import com.example.comunity.dto.board.BoardDto;
import com.example.comunity.dto.board.BoardUploadDto;
import com.example.comunity.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/board")
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

    @GetMapping("/boards")
    public CollectionModel<EntityModel<BoardDto>> findAll() {
        List<EntityModel<BoardDto>> boards = new ArrayList<>();
        for (Board board : boardService.findAll()) {
            BoardDto boardDto = new BoardDto(
                    board.getBoardId(),
                    board.getUser(),
                    board.getCategory(),
                    board.getTitle(),
                    board.getContent(),
                    board.getBoardUri(),
                    board.getUploadFiles());
            EntityModel<BoardDto> boardDtoEntityModel = EntityModel.of(boardDto,
                    linkTo(methodOn(BoardController.class).findAll()).withRel("boards"));
            boards.add(boardDtoEntityModel);
        }

        return CollectionModel.of(boards,
                linkTo(methodOn(BoardController.class).findAll()).withSelfRel());
    }
}
