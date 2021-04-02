package com.example.comunity.repository.board;

import com.example.comunity.domain.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final EntityManager em;


    /**
     * 게시판 생성
     */
    @Override
    public Board upload(final Board board) {
        em.persist(board);
        return board;
    }

    /**
     * 특정 게시판 삭제
     */
    @Override
    public void delete(final Long boardId) {
        Board findBoard = findBoardById(boardId);
        em.remove(findBoard);
    }

    @Override
    public void deleteAllByIds(final List<Long> ids) {
        em.createQuery(
                "delete from Board b " +
                        " where b.boardId in :ids")
                .setParameter("ids", ids)
                .executeUpdate();
    }

    /**
     * 게시판 번호로 하나의 게시판 조회
     */
    @Override
    public Board findBoardById(final Long boardId) {
        return em.find(Board.class, boardId);
    }

    /**
     * 특정 카테고리에 포함된 하나의 게시판 조회 (게시판 번호로)
     * 일대다 패치조인 포함 (boardId는 겹칠 수 없기 떄문에 distinct 불필요)
     *
     * @param boardId      조회할 게시판의 번호
     * @param categoryName 조회할 게시판이 포함된 카테고리 이름
     */
    @Override
    public Board findBoardByIdWithCategory(final Long boardId, final String categoryName) {
        try {
            return em.createQuery(
                    "select b from Board b" +
                            " join fetch b.user" +
                            " join fetch b.category c" +
                            " where b.boardId = :boardId" +
                            " and c.categoryName = :categoryName", Board.class)
                    .setParameter("boardId", boardId)
                    .setParameter("categoryName", categoryName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            String message = nre.getMessage();
            System.out.println("nre(message) = " + message);
            return null;
        }
    }

    @Override
    public List<Board> findAllWithUser(final String userId) {
        return em.createQuery(
                "select b from Board b" +
                        " join fetch b.user" +
                " where b.user.userId = :userId", Board.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
