package com.example.comunity.repository;

import com.example.comunity.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;

    public Comment apply(final Comment comment) {
        em.persist(comment);
        em.flush();
        return comment;
    }

    public void delete(final Comment comment) {
        em.remove(comment);
        em.flush();
    }

    public List<Comment> findAll(final Long boardId) {
        return em.createQuery(
                "select c from Comment c" +
                        " where c.board.boardId = :boardId", Comment.class)
                .setParameter("boardId", boardId)
                .getResultList();
    }

    public Comment findById(final Long commentId) {
        return em.find(Comment.class, commentId);
    }
}
