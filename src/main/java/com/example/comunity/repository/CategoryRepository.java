package com.example.comunity.repository;

import com.example.comunity.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepository {

    private final EntityManager em;

    /**
     * 카테고리 생성
     */
    public Long create(Category category) {
        em.persist(category);
        return category.getCategoryId();
    }

    /**
     * 모든 카테고리 조회
     */
    public List<Category> findAll() {
        return em.createQuery("select c from Category c", Category.class)
                .getResultList();
    }


}
