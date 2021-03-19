package com.example.comunity.repository.comment;

import com.example.comunity.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final EntityManager em;

    public Comment apply(final Comment comment) {
        em.persist(comment);
        return comment;
    }

    public void delete(final Long commentId) {
        Comment findComment = findCommentById(commentId);
        em.remove(findComment);
    }

    public List<Comment> findAll(final Long boardId) {
        return em.createQuery(
                "select c from Comment c" +
                        " join fetch c.user" +
                        " where c.board.boardId = :boardId" +
                        " order by c.commentId asc", Comment.class)
                .setParameter("boardId", boardId)
                .getResultList();
    }

    public Comment findCommentById(final Long commentId) {
        return em.find(Comment.class, commentId);
    }

    public void deleteAllByIds(List<Long> ids) {
        em.createQuery(
                "delete from Comment c " +
                        " where c.commentId in :ids")
                .setParameter("ids", ids)
                .executeUpdate();
    }
}
