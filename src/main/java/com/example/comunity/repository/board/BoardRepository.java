package com.example.comunity.repository.board;

import com.example.comunity.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    /**
     * jpql fetch join
     * @EntityGraph 사용 대신, Count 쿼리를 명시적으로 생성
     *
     * @param pageable .
     */
    @Query(value = "select b from Board b join fetch b.category c where c.categoryName = :categoryName",
            countQuery = "select count(b) from Board b join b.category c where c.categoryName = :categoryName")
    Page<Board> findAllWithCategory(@Param("categoryName") final String categoryName, final Pageable pageable);
}
