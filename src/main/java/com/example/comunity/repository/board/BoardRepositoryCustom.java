package com.example.comunity.repository.board;

import com.example.comunity.domain.Board;

public interface BoardRepositoryCustom {

    Board upload(Board board);

    void delete(Long boardId);

    Board findBoardById(Long boardId);
    Board findBoardByIdWithCategory(Long boardId, String categoryName);
}
