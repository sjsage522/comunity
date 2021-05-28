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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public Comment apply(final User loginUser, final Long boardId, final CommentApplyRequest commentApplyRequest) {

        /**
         * 존재하는 게시글에만 답글을 달 수 있음
         */
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoMatchBoardInfoException("존재하지 않는 게시글입니다."));

        Comment comment = Comment.from(loginUser, findBoard, commentApplyRequest.getContent());

        /**
         * 대댓글 작성 부분
         */
        Long parentId = commentApplyRequest.getParentId();
        if (parentId != null) {
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new NoMatchCommentInfoException("존재하지 않는 답글입니다."));
            parentComment.addChildComment(comment);
        }

        return commentRepository.save(comment);
    }

    @Transactional
    public void delete(final User loginUser, final Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoMatchCommentInfoException("존재하지 않는 답글입니다."));
        checkValid(loginUser, findComment);

        commentRepository.delete(findComment);
    }

    @Transactional
    public Comment update(User loginUser, Long commentId, CommentUpdateRequest commentUpdateDto) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoMatchCommentInfoException("존재하지 않는 답글입니다."));
        checkValid(loginUser, findComment);

        findComment.changeContent(commentUpdateDto.getContent());

        return findComment;
    }

    private void checkValid(User loginUser, Comment findComment) {
        if (!findComment.getUser().getUserId().equals(loginUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 답글을 삭제할 수 없습니다.");
    }

    public List<Comment> findAll(final Long boardId, final Integer pageNumber) {
        return commentRepository.findAll(boardId, PageRequest.of(pageNumber, 10)).stream()
                .collect(Collectors.toList());
    }
}
