package com.example.comunity.repository;

import com.example.comunity.domain.Category;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.MethodName.class)
@DisplayName("카테고리 레포지토리 테스트")
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("테스트 01. 카테고리 생성 테스트")
    void _01_create() {

        //given
        Category newCategory = Category.from("coding");

        //when
        categoryRepository.create(newCategory);

        em.flush();
        em.clear();

        //then
        assertThat(categoryRepository.findByName("coding").getCategoryId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("테스트 02. 카테고리 삭제 테스트")
    void _02_delete() {

        //given
        Category c1 = Category.from("coding");

        categoryRepository.create(c1);

        em.flush();
        em.clear();

        //when
        categoryRepository.delete(2L);

        //then
        assertThat(categoryRepository.findById(2L)).isNull();
    }

    @Test
    @DisplayName("테스트 03. 카테고리 조회 테스트 (by id)")
    void _03_findById() {

        //given
        Category newCategory = Category.from("coding");

        categoryRepository.create(newCategory);

        em.flush();
        em.clear();

        //when
        Category findCategory = categoryRepository.findById(3L);

        //then
        assertThat(findCategory.getCategoryName()).isEqualTo("coding");
    }

    @Test
    @DisplayName("테스트 04. 모든 카테고리 조회 테스트")
    void _04_findAll() {

        //given
        Category c1 = Category.from("coding");
        Category c2 = Category.from("reading");

        categoryRepository.create(c1);
        categoryRepository.create(c2);

        em.flush();
        em.clear();

        //when
        List<Category> categories = categoryRepository.findAll();

        //then
        assertThat(categories.size()).isEqualTo(2);
        assertThat(categories.get(0).getCategoryName()).isEqualTo("coding");
        assertThat(categories.get(0).getCategoryId()).isEqualTo(4L);
        assertThat(categories.get(1).getCategoryName()).isEqualTo("reading");
        assertThat(categories.get(1).getCategoryId()).isEqualTo(5L);
    }
}