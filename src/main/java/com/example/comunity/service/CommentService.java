package com.example.comunity.service;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Comment;
import com.example.comunity.domain.User;
import com.example.comunity.dto.comment.CommentApplyDto;
import com.example.comunity.dto.comment.CommentUpdateDto;
import com.example.comunity.exception.NoMatchBoardInfoException;
import com.example.comunity.exception.NoMatchCommentInfoException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.board.BoardRepository;
import com.example.comunity.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public Comment apply(final User loginUser, final Long boardId, final CommentApplyDto commentApplyDto) {

        Board findBoard = boardRepository.findBoardById(boardId);

        if (findBoard == null) throw new NoMatchBoardInfoException("존재하지 않는 게시글입니다.");

        Comment comment = Comment.createComment(loginUser, findBoard, commentApplyDto.getContent());

        Long parentId = commentApplyDto.getParentId();
        if (parentId != null) {
            Comment parentComment = commentRepository.findCommentById(parentId);
            if (parentComment == null) throw new NoMatchCommentInfoException("존재하지 않는 답글입니다.");
            parentComment.addChildComment(comment);
        }

        return commentRepository.apply(comment);
    }

    public Comment findById(final Long id) {
        return commentRepository.findCommentById(id);
    }

    @Transactional
    public void delete(final User loginUser, final Long commentId) {
        Comment findComment = commentRepository.findCommentById(commentId);

        checkValid(loginUser, findComment);

        commentRepository.delete(commentId);
    }

    @Transactional
    public Comment update(User loginUser, Long id, CommentUpdateDto commentUpdateDto) {

        Comment findComment = commentRepository.findCommentById(id);

        checkValid(loginUser, findComment);

        findComment.changeContent(commentUpdateDto.getContent());

        return findComment;
    }

    private void checkValid(User loginUser, Comment findComment) {
        if (findComment == null) throw new NoMatchCommentInfoException("존재하지 않는 답글입니다.");

        if (!findComment.getUser().getUserId().equals(loginUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 답글을 삭제할 수 없습니다.");
    }

    public List<Comment> findAll(final Long boardId, final Integer pageNumber) {
        return commentRepository.findAll(boardId, PageRequest.of(pageNumber, 2)).stream()
                .collect(Collectors.toList());
    }
}
