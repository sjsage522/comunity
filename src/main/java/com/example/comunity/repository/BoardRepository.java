package com.example.comunity.repository;

import com.example.comunity.domain.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final EntityManager em;

    /**
     * 게시판 생성
     */
    public Long upload(final Board board) {
        em.persist(board);
        em.flush();
        return board.getBoardId();
    }

    /**
     * 특정 게시판 삭제
     */
    public void delete(final Long boardId) {
        Board findBoard = findBoardById(boardId);
        em.remove(findBoard);
    }

    /**
     * 모든 게시판 조회
     */
    public List<Board> findAll() {
        return em.createQuery("select b from Board b", Board.class)
                .getResultList();
    }

    /**
     * 게시판 번호로 하나의 게시판 조회
     */
    public Board findBoardById(final Long boardId) {
        return em.find(Board.class, boardId);
    }

    /**
     * 특정 카테고리에 포함된 모든 게시판 조회
     */
    public List<Board> findAllWithCategory(final String categoryName) {
        return em.createQuery(
                "select b from Board b" +
                        " join fetch b.category c" +
                        " where c.name = :categoryName", Board.class)
                .setParameter("categoryName", categoryName)
                .getResultList();
    }

    /**
     * 특정 카테고리에 포함된 하나의 게시판 조회 (게시판 번호로)
     *
     * @param boardId      조회할 게시판의 번호
     * @param categoryName 조회할 게시판이 포함된 카테고리 이름
     */
    public Board findBoardByIdWithCategory(final Long boardId, final String categoryName) {
        try {
            return em.createQuery(
                    "select b from Board b" +
                            " join fetch b.category c" +
                            " where b.boardId = :boardId" +
                            " and c.name = :categoryName", Board.class)
                    .setParameter("boardId", boardId)
                    .setParameter("categoryName", categoryName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            String message = nre.getMessage();
            System.out.println("nre(message) = " + message);
            return null;
        }
    }

    /**
     * 사용자가 작성한 게시판 전부 조회
     */
    public List<Board> findAllWithUser(final String userId) {
        return em.createQuery(
                "select b from Board b" +
                        " join fetch b.user u" +
                        " where u.userId = :userId", Board.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * 사용자가 작성한 특정 카테고리에 포함된 게시판 전부 조회
     */
    public List<Board> findBoardByIdWithCategoryAndUser(final Long boardId, final String categoryName, final String userId) {
        return em.createQuery(
                "select b from Board b" +
                        " join fetch b.category c" +
                        " join fetch b.user u" +
                        " where b.boardId = :boardId" +
                        " and c.name = :categoryName" +
                        " and u.userId = :userId", Board.class)
                .setParameter("boardId", boardId)
                .setParameter("categoryName", categoryName)
                .setParameter("userId", userId)
                .getResultList();
    }
}
