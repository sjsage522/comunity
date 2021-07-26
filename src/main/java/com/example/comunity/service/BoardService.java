package com.example.comunity.service;

import com.example.comunity.domain.*;
import com.example.comunity.dto.board.BoardUpdateRequest;
import com.example.comunity.dto.board.BoardUploadRequest;
import com.example.comunity.exception.NoMatchBoardInfoException;
import com.example.comunity.exception.NoMatchCategoryInfoException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.FileRepository;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CommentRepository;
import com.example.comunity.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;
    private final FileUtils fileUtils;

    public List<Board> findAll(final Integer pageNumber) {

        /* 10개씩 페이징 */
        Page<Board> boards = boardRepository.findAllByOrderByBoardIdDesc(PageRequest.of(pageNumber, 10));
        return boards.stream().collect(Collectors.toList());
    }

    @Transactional
    public Board upload(final BoardUploadRequest boardUploadRequest, final User loginUser, final MultipartFile[] files) {
        Category findCategory = findCategoryByName(boardUploadRequest.getCategoryName());

        Board newBoard = Board.from(
                loginUser,
                findCategory,
                boardUploadRequest.getTitle(),
                boardUploadRequest.getContent());

        /**
         * 새로운 게시글 생성
         */
        Board uploadedBoard = boardRepository.save(newBoard);

        /**
         * files 가 null 이 아니라면, 파일을 디스크에 저장
         */
        List<UploadFile> fileList = fileUtils.uploadFiles(files, uploadedBoard);
        if (!CollectionUtils.isEmpty(fileList)) {
            fileRepository.saveAll(fileList);
            uploadedBoard.uploadFiles(fileList);
        }

        return uploadedBoard;
    }

    public List<Board> findAllWithCategory(final String name, final Integer pageNumber) {

        /* 10개씩 페이징 */
        Page<Board> boards = boardRepository.findAllWithCategory(name, PageRequest.of(pageNumber, 10));
        return boards.stream().collect(Collectors.toList());
    }

    public Board findByIdWithCategory(final Long id, final String name) {
        return boardRepository.findByBoardIdAndCategoryName(id, name)
                .orElseThrow(NoMatchBoardInfoException::new);
    }

    @Transactional
    public void delete(final Long boardId, final String categoryName, final User loginUser) {

        Board findBoard = boardRepository.findByBoardIdAndCategoryName(boardId, categoryName)
                .orElseThrow(NoMatchBoardInfoException::new);

        User findUser = findBoard.getUser();
        if (!findUser.getUserId().equals(loginUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 게시글을 삭제할 수 없습니다.");

        /**
         * 게시글을 삭제하기 전에 먼저 연관된 첨부파일과 댓글들을 삭제해야 한다. (참조 무결성)
         */
        UserService.deleteRelatedToBoard(boardId, fileRepository, commentRepository);

        boardRepository.delete(findBoard);
    }

    @Transactional
    public Board update(final Long boardId, final String categoryName, final BoardUpdateRequest boardUpdateRequest, final User loginUser) {

        /**
         * 수정하고자 하는 게시글을 찾는다.
         */
        Board findBoard = boardRepository.findByBoardIdAndCategoryName(boardId, categoryName)
                .orElseThrow(NoMatchBoardInfoException::new);

        /**
         * 다른 사용자는 게시글을 수정할 수 없도록 처리
         * loginUser -> 세션에서 꺼내온 사용자 정보
         */
        User findUser = findBoard.getUser();
        if (!findUser.getUserId().equals(loginUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 게시글을 수정할 수 없습니다.");

        /**
         * 영속성 컨텍스트의 dirty check
         */
        Category changedCategory = findCategoryByName(boardUpdateRequest.getCategoryName());
        findBoard.changeBoard(boardUpdateRequest.getTitle(), boardUpdateRequest.getContent(), changedCategory);

        return findBoard;
    }

    private Category findCategoryByName(final String name) {
        return categoryRepository.findByCategoryName(name)
                .orElseThrow(NoMatchCategoryInfoException::new);
    }
}
