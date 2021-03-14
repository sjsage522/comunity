package com.example.comunity.service;

import com.example.comunity.domain.*;
import com.example.comunity.dto.board.BoardUpdateDto;
import com.example.comunity.dto.board.BoardUploadDto;
import com.example.comunity.dto.file.UploadFileDto;
import com.example.comunity.exception.NoMatchBoardInfoException;
import com.example.comunity.exception.NoMatchCategoryInfoException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.CommentRepository;
import com.example.comunity.repository.FileRepository;
import com.example.comunity.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    @Transactional
    public Board upload(final BoardUploadDto boardUploadDto, final User loginUser, final MultipartFile[] files) {
        Category findCategory = findCategoryByName(boardUploadDto.getCategoryName());

        Board newBoard = Board.createBoard(
                loginUser,
                findCategory,
                boardUploadDto.getTitle(),
                boardUploadDto.getContent());

        Board uploadedBoard = boardRepository.upload(newBoard);

        List<UploadFileDto> fileDtoList = fileUtils.uploadFiles(files, uploadedBoard);

        List<UploadFile> fileList = new ArrayList<>();
        for (UploadFileDto uploadFileDto : fileDtoList) {
            fileList.add(UploadFile.createFile(
                    newBoard,
                    uploadFileDto.getOriginalFileName(),
                    uploadFileDto.getStoredFileName(),
                    uploadFileDto.getFileSize()));
        }

        if (!CollectionUtils.isEmpty(fileList)) {
            fileRepository.uploadFiles(fileList);
            uploadedBoard.uploadFiles(fileList);
        }

        return uploadedBoard;
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
    public void delete(final Long boardId, final String categoryName, final User loginUser) {

        Board findBoard = boardRepository.findBoardByIdWithCategory(boardId, categoryName);
        if (findBoard == null) throw new NoMatchBoardInfoException("존재하지 않는 게시글 입니다.");

        User findUser = findBoard.getUser();
        if (!findUser.getUserId().equals(loginUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 게시글을 삭제할 수 없습니다.");

        List<UploadFile> uploadFiles = fileRepository.findAll();
        List<Long> fileIds = uploadFiles.stream()
                .map(UploadFile::getUploadFileId)
                .collect(Collectors.toList());
        fileRepository.deleteAllByIds(fileIds);

        List<Comment> comments = commentRepository.findAll(boardId);
        List<Long> commentIds = comments.stream()
                .map(Comment::getCommentId)
                .collect(Collectors.toList());
        commentRepository.deleteAllByIds(commentIds);

        boardRepository.delete(boardId);
    }

    @Transactional
    public Board update(final Long boardId, final String categoryName, final BoardUpdateDto boardUpdateDto, final User loginUser) {

        Board findBoard = boardRepository.findBoardByIdWithCategory(boardId, categoryName);

        if (findBoard == null) throw new NoMatchBoardInfoException("존재하지 않는 게시글 입니다.");

        User findUser = findBoard.getUser();
        if (!findUser.getUserId().equals(loginUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 게시글을 수정할 수 없습니다.");

        Category changedCategory = findCategoryByName(boardUpdateDto.getCategoryName());

        findBoard.changeBoard(boardUpdateDto.getTitle(), boardUpdateDto.getContent(), changedCategory);

        return findBoard;
    }

    private Category findCategoryByName(final String name) {
        Category findCategory = categoryRepository.findByName(name);
        if (findCategory == null) throw new NoMatchCategoryInfoException("존재하지 않는 카테고리명 입니다.");
        return findCategory;
    }
}
