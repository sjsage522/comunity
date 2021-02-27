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

    public Long create(final Comment comment) {
        em.persist(comment);
        return comment.getCommentId();
    }

    public int delete(final Long commentId) {
        return em.createQuery(
                "delete from Comment c" +
                " where c.commentId = :commentId")
                .setParameter("commentId", commentId)
                .executeUpdate();
    }

    public List<Comment> findAll() {
        return em.createQuery("select c from Comment c", Comment.class)
                .getResultList();
    }

    public Comment findById(final Long commentId) {
        return em.find(Comment.class, commentId);
    }
}
