package com.example.comunity.service;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Comment;
import com.example.comunity.domain.User;
import com.example.comunity.dto.comment.CommentApplyDto;
import com.example.comunity.dto.comment.CommentUpdateDto;
import com.example.comunity.exception.NoMatchCommentInfoException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.board.BoardRepository;
import com.example.comunity.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public Comment apply(final User loginUser, final Long boardId, final CommentApplyDto commentApplyDto) {

        Board findBoard = boardRepository.findBoardById(boardId);
        Comment comment = Comment.createComment(loginUser, findBoard, commentApplyDto.getContent());

        Long parentId = commentApplyDto.getParentId();
        if (parentId != null) {
            Comment parentComment = commentRepository.findById(parentId);
            if (parentComment == null) throw new NoMatchCommentInfoException("존재하지 않는 답글입니다.");
            parentComment.addChildComment(comment);
        }

        return commentRepository.apply(comment);
    }

    public Comment findById(final Long id) {
        return commentRepository.findById(id);
    }

    @Transactional
    public void delete(final User loginUser, final Long commentId) {
        Comment findComment = commentRepository.findById(commentId);

        checkValid(loginUser, findComment);

        commentRepository.delete(findComment);
    }

    @Transactional
    public Comment update(User loginUser, Long id, CommentUpdateDto commentUpdateDto) {

        Comment findComment = commentRepository.findById(id);

        checkValid(loginUser, findComment);

        findComment.changeContent(commentUpdateDto.getContent());

        return findComment;
    }

    private void checkValid(User loginUser, Comment findComment) {
        if (findComment == null) throw new NoMatchCommentInfoException("존재하지 않는 답글입니다.");

        if (!findComment.getUser().getUserId().equals(loginUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 답글을 삭제할 수 없습니다.");
    }

    public List<Comment> findAll(final Long boardId) {
        return commentRepository.findAll(boardId);
    }
}
