package com.example.comunity.service;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.User;
import com.example.comunity.dto.board.BoardUploadDto;
import com.example.comunity.exception.NoMatchBoardInfoException;
import com.example.comunity.exception.NoMatchCategoryInfoException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    @Transactional
    public Long upload(final BoardUploadDto boardUploadDto, final User loginUser) {
        Category findCategory = findCategoryByName(boardUploadDto.getCategoryName());

        boardUploadDto.setUser(loginUser);
        boardUploadDto.setUserId(loginUser.getUserId());

        Board newBoard = Board.createBoard(
                boardUploadDto.getUser(),
                findCategory,
                boardUploadDto.getTitle(),
                boardUploadDto.getContent());

        newBoard.uploadFiles();

        return boardRepository.upload(newBoard);
    }

    public List<Board> findAllWithCategory(final String name) {
        return boardRepository.findAllWithCategory(name);
    }

    public Board findByIdWithCategory(final Long id, final String name) {
        Board findBoard = boardRepository.findBoardByIdWithCategory(id, name);
        if (findBoard == null) throw new NoMatchBoardInfoException("존재하지 않는 게시글 입니다.");
        return findBoard;
    }

    @Transactional
    public int delete(final Long boardId, final String categoryName, final User loginUser) {

        Category findCategory = findCategoryByName(categoryName);
        if (findCategory != null) {
            Board findBoard = boardRepository.findBoardById(boardId);
            if (findBoard == null) throw new NoMatchBoardInfoException("존재하지 않는 게시글 입니다.");
            User findUser = findBoard.getUser();
            if (findUser == null) throw new NoMatchUserInfoException("존재하지 않는 사용자 입니다.");
            if (findUser.getUserId().equals(loginUser.getUserId())) {
                return boardRepository.delete(boardId);
            } else {
                throw new NoMatchUserInfoException("다른 사용자의 게시글을 삭제할 수 없습니다.");
            }
        }

        return 0;
    }

    public Category findCategoryByName(final String name) {
        Category findCategory = categoryRepository.findByName(name);
        if (findCategory == null) throw new NoMatchCategoryInfoException("존재하지 않는 카테고리명 입니다.");
        return findCategory;
    }
}
