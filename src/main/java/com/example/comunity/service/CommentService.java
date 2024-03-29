package com.example.comunity.service;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Comment;
import com.example.comunity.domain.User;
import com.example.comunity.dto.comment.CommentApplyRequest;
import com.example.comunity.dto.comment.CommentUpdateRequest;
import com.example.comunity.exception.NoMatchBoardInfoException;
import com.example.comunity.exception.NoMatchCommentInfoException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public Comment apply(
            final User loginUser,
            final Long boardId,
            final CommentApplyRequest commentApplyRequest) {
        // 존재하는 게시글에만 답글을 달 수 있음
        final Board targetBoard = findBoardById(boardId);

        final Comment newComment = Comment.of(loginUser, targetBoard, commentApplyRequest.getContent());

        // 대댓글 작성 부분
        applyToParent(newComment, commentApplyRequest.getParentId());

        return commentRepository.save(newComment);
    }

    @Transactional
    public void delete(
            final User loginUser,
            final Long commentId) {
        final Comment deleteComment = findCommentById(commentId);
        compareUser(loginUser, deleteComment.getUser());

        commentRepository.delete(deleteComment);
    }

    @Transactional
    public Comment update(
            final User loginUser,
            final Long commentId,
            final CommentUpdateRequest commentUpdateRequest) {
        final Comment updateComment = findCommentById(commentId);
        compareUser(loginUser, updateComment.getUser());

        updateComment.changeContent(commentUpdateRequest.getContent());

        return updateComment;
    }

    @Transactional(readOnly = true)
    public List<Comment> findAll(
            final Long boardId,
            final int page) {
        findBoardById(boardId);
        return commentRepository.findAllByBoardIdIfParentIsNullWithPaging(boardId, PageRequest.of(page, 10))
                .stream()
                .collect(Collectors.toList());
    }

    private void compareUser(
            final User loginUser,
            final User commentUser) {
        if (!commentUser.equals(loginUser))
            throw new NoMatchUserInfoException("다른 사용자의 답글을 삭제할 수 없습니다.");
    }

    private Board findBoardById(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(NoMatchBoardInfoException::new);
    }

    private Comment findCommentById(final Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(NoMatchCommentInfoException::new);
    }

    private void applyToParent(
            final Comment comment,
            final Long parentId) {
        if (parentId != null) {
            final Comment parentComment = findCommentById(parentId);
            parentComment.addChildComment(comment);
        }
    }
}
