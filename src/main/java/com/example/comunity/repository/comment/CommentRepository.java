package com.example.comunity.repository.comment;

import com.example.comunity.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Query(value = "select c from Comment c" +
            " join fetch c.user" +
            " where c.board.boardId = :boardId" +
            " and c.parent is null" +
            " order by c.commentId asc",
            countQuery = "select count(c) from Comment c where c.board.boardId = :boardId")
    Page<Comment> findAll(@Param("boardId") final Long boardId, final Pageable pageable);
}
