package com.example.comunity.repository;

import com.example.comunity.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select c from Comment c" +
            " join fetch c.user" +
            " where c.board.boardId = :boardId" +
            " and c.parent is null" +
            " order by c.commentId asc",
            countQuery = "select count(c) from Comment c where c.board.boardId = :boardId")
    Page<Comment> findAll(final Long boardId, final Pageable pageable);


    @EntityGraph(attributePaths = {"user"})
    @Query("select c from Comment c order by c.commentId asc")
    List<Comment> findAllByBoard_BoardId(Long boardId);

    @Modifying
    @Query("delete from Comment c where c.commentId in :ids")
    void deleteWithIds(List<Long> ids);
}
