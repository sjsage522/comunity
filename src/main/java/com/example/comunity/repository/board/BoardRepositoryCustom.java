package com.example.comunity.repository.board;

import com.example.comunity.domain.Board;

import java.util.List;

public interface BoardRepositoryCustom {

    Board upload(Board board);

    void delete(Long boardId);

    Board findBoardById(Long boardId);
    Board findBoardByIdWithCategory(Long boardId, String categoryName);

    public List<Board> findAllWithUser(String userId);

    void deleteAllByIds(final List<Long> ids);
}
