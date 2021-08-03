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
            " where c.parent is null" +
            " and c.board.id = :boardId" +
            " order by c.id asc")
    @EntityGraph(attributePaths = {"user"})
    Page<Comment> findAllByBoardIdIfParentIsNullWithPaging(final Long boardId, final Pageable pageable);

    List<Comment> findAllByBoardId(final Long boardId);

    @Modifying
    @Query("delete from Comment c where c.id in :ids")
    void deleteWithIds(final List<Long> ids);
}
