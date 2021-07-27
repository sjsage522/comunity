package com.example.comunity.service;

import com.example.comunity.domain.*;
import com.example.comunity.dto.board.BoardUpdateRequest;
import com.example.comunity.dto.board.BoardUploadRequest;
import com.example.comunity.exception.NoMatchBoardInfoException;
import com.example.comunity.exception.NoMatchCategoryInfoException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.CommentRepository;
import com.example.comunity.repository.FileRepository;
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
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;
    private final FileUtils fileUtils;

    @Transactional(readOnly = true)
    public List<Board> findAll(
            final int page) {
        /* 10개씩 페이징 */
        Page<Board> boards = boardRepository.findAllWithPaging(PageRequest.of(page, 10));
        return boards.stream().collect(Collectors.toList());
    }

    @Transactional
    public Board upload(
            final BoardUploadRequest boardUploadRequest,
            final User loginUser,
            final MultipartFile[] files) {
        Board newBoard = getBoard(boardUploadRequest, loginUser);

        // 새로운 게시글 생성
        Board uploadedBoard = boardRepository.save(newBoard);

        // files 가 null 이 아니라면, 파일을 디스크에 저장
        List<UploadFile> fileList = fileUtils.uploadFiles(files, uploadedBoard);
        if (!CollectionUtils.isEmpty(fileList)) {
            fileRepository.saveAll(fileList);
            uploadedBoard.uploadFiles(fileList);
        }

        return uploadedBoard;
    }

    @Transactional(readOnly = true)
    public List<Board> findAllWithCategory(
            final String categoryName,
            final int page) {
        /* 10개씩 페이징 */
        Page<Board> boards = boardRepository.findAllByCategoryNameWithPaging(categoryName, PageRequest.of(page, 10));
        return boards.stream().collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Board findByIdWithCategory(
            final Long boardId,
            final String categoryName) {
        return boardRepository.findByBoardIdAndCategoryName(boardId, categoryName)
                .orElseThrow(NoMatchBoardInfoException::new);
    }

    @Transactional(readOnly = true)
    public Board findById(
            final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(NoMatchBoardInfoException::new);
    }

    @Transactional
    public void delete(
            final Long boardId,
            final String categoryName,
            final User loginUser) {
        Board deleteBoard = findBoardByIdAndCategoryName(boardId, categoryName);
        User findUser = deleteBoard.getUser();

        compareUser(loginUser, findUser, "다른 사용자의 게시글을 삭제할 수 없습니다.");

        // 게시글을 삭제하기 전에 먼저 연관된 첨부파일과 댓글들을 삭제해야 한다. (참조 무결성)
        deleteComments(boardId);
        boardRepository.delete(deleteBoard);
    }

    @Transactional
    public Board update(
            final Long boardId,
            final String categoryName,
            final BoardUpdateRequest boardUpdateRequest,
            final User loginUser) {
        // 수정하고자 하는 게시글을 찾는다.
        Board findBoard = findBoardByIdAndCategoryName(boardId, categoryName);

        // 다른 사용자는 게시글을 수정할 수 없도록 처리
        // loginUser -> 세션에서 꺼내온 사용자 정보
        User findUser = findBoard.getUser();
        compareUser(loginUser, findUser, "다른 사용자의 게시글을 수정할 수 없습니다.");

        // 영속성 컨텍스트의 dirty check
        Category changedCategory = findCategoryByName(boardUpdateRequest.getCategoryName());
        findBoard.changeBoard(boardUpdateRequest.getTitle(), boardUpdateRequest.getContent(), changedCategory);

        return findBoard;
    }

    private Category findCategoryByName(final String categoryName) {
        return categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(NoMatchCategoryInfoException::new);
    }

    private Board findBoardByIdAndCategoryName(Long boardId, String categoryName) {
        return boardRepository.findByBoardIdAndCategoryName(boardId, categoryName)
                .orElseThrow(NoMatchBoardInfoException::new);
    }

    private void deleteComments(Long boardId) {
        final List<Comment> commentList = commentRepository.findAllByBoardId(boardId);

        for (Comment comment : commentList) {
            System.out.println("comment = " + comment + ", class = " + comment.getClass());
        }

        List<Long> commentIds = commentList.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
        commentRepository.deleteWithIds(commentIds);
    }

    private void compareUser(User loginUser, User findUser, String errorMessage) {
        if (!findUser.getUserId().equals(loginUser.getUserId()))
            throw new NoMatchUserInfoException(errorMessage);
    }

    private Board getBoard(BoardUploadRequest boardUploadRequest, User loginUser) {
        Category findCategory = findCategoryByName(boardUploadRequest.getCategoryName());
        return Board.from(
                loginUser,
                findCategory,
                boardUploadRequest.getTitle(),
                boardUploadRequest.getContent());
    }
}
