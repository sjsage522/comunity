package com.example.comunity.repository;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepository {

    private final BoardRepository boardRepository;
    private final EntityManager em;

    /**
     * 카테고리 생성
     */
    public Long create(final Category category) {
        em.persist(category);
        return category.getCategoryId();
    }

    /**
     * 카테고리 삭제 (id)
     */
    public int delete(final Long categoryId) {
//        Category findCategory = this.findById(categoryId);
//
//        if (findCategory == null) return null;
//
//        List<Board> boardsWithCategory = boardRepository.findAllWithCategory(findCategory.getName());
//
//        List<Long> boardIds = new ArrayList<>();
//        boardsWithCategory.forEach(board -> {
//            boardIds.add(board.getBoardId());
//        });
//
//        em.createQuery(
//                "delete from Board b" +
//                        " where b.boardId in :boardIds")
//                .setParameter("boardIds", boardIds)
//                .executeUpdate();
//
//        em.remove(findCategory);
//        return findCategory.getCategoryId();
        return em.createQuery(
                "delete from Category c" +
                        " where c.categoryId = :categoryId")
                .setParameter("categoryId", categoryId)
                .executeUpdate();
    }

    /**
     * 모든 카테고리 조회
     */
    public List<Category> findAll() {
        return em.createQuery("select c from Category c", Category.class)
                .getResultList();
    }

    /**
     * 카테고리 단건 조회 (id)
     */
    public Category findById(final Long categoryId) {
        return em.find(Category.class, categoryId);
    }

    /**
     * 카테고리 단건 조회 (name)
     */
    public Category findByName(final String categoryName) {
        try {
            return em.createQuery(
                    "select c from Category c" +
                            " where c.name = :categoryName", Category.class)
                    .setParameter("categoryName", categoryName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            String message = nre.getMessage();
            System.out.println("nre(message) = " + message);
            return null;
        }
    }
}
