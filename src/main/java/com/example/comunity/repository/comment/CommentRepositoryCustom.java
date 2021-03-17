package com.example.comunity.repository.comment;

import com.example.comunity.domain.Comment;

import java.util.List;

public interface CommentRepositoryCustom {

    Comment apply(final Comment comment);

    void delete(final Long commentId);

    List<Comment> findAll(final Long boardId);

    Comment findCommentById(final Long commentId);

    void deleteAllByIds(final List<Long> ids);
}
