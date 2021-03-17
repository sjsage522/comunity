package com.example.comunity.repository.board;

import com.example.comunity.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    /**
     * jpql fetch join
     *
     * @param pageable .
     * @EntityGraph 사용 대신, Count 쿼리를 명시적으로 생성
     */
    @Query(value = "select b " +
            "from Board b " +
            "join fetch b.category c " +
            "join fetch b.user u " +
            "where c.categoryName = :categoryName " +
            "order by b.boardId desc",
            countQuery = "select count(b) from Board b join b.category c where c.categoryName = :categoryName")
    Page<Board> findAllWithCategory(@Param("categoryName") final String categoryName, final Pageable pageable);


    /**
     * uploadFiles는 fetch join하면 안됨!
     * 일대다 관계에서 fetch join시 페이징을 할 수 없음 (데이터의 정합성)
     *
     * @param pageable .
     */
    @Query(value = "select b " +
            "from Board b " +
            "join fetch b.category c " +
            "join fetch b.user u " +
            "order by b.boardId desc",
            countQuery = "select count(b) from Board b")
    Page<Board> findAllByOrderByBoardIdDesc(final Pageable pageable);
}
