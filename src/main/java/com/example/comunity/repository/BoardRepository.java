package com.example.comunity.repository;

import com.example.comunity.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * jpql fetch join
     */
    @Query(value = "select b " +
            "from Board b " +
            "join fetch b.category c " +
            "where c.categoryName = :categoryName " +
            "order by b.id desc",
            countQuery = "select count(b) from Board b join b.category c where c.categoryName = :categoryName")
    @EntityGraph(attributePaths = {"user"})
    Page<Board> findAllByCategoryNameWithPaging(final String categoryName, final Pageable pageable);


    /**
     * uploadFiles는 fetch join하면 안됨!
     * 일대다 관계에서 fetch join시 페이징을 할 수 없음 (데이터의 정합성)
     *
     * @param pageable .
     */
    @Query(value = "select b " +
            "from Board b " +
            "order by b.id desc",
            countQuery = "select count(b) from Board b")
    @EntityGraph(attributePaths = {"user", "category"})
    Page<Board> findAllWithPaging(final Pageable pageable);

    @Modifying
    @Query("delete from Board b where b.id in :ids")
    void deleteWithIds(List<Long> ids);

    @Query("select b from Board b" +
            " join fetch b.category c" +
            " where b.id = :boardId" +
            " and c.categoryName = :categoryName")
    @EntityGraph(attributePaths = {"user"})
    Optional<Board> findByBoardIdAndCategoryName(Long boardId, String categoryName);

    /**
     * 간단한 fetch join 의 경우 @EntityGraph 애노테이션 활용
     */
    @EntityGraph(attributePaths = {"user"})
    List<Board> findAllByUser_UserId(String userId);
}
