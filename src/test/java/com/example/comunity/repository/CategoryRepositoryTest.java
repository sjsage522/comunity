package com.example.comunity.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

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
    }

    @Test
    @DisplayName("테스트 02. 카테고리 조회 테스트 (by id)")
    void _02_findById() {
    }

    @Test
    @DisplayName("테스트 03. 카테고리 조회 테스트 (by name)")
    void _03_findByName() {
    }

    @Test
    @DisplayName("테스트 04. 모든 카테고리 조회 테스트")
    void _04_findAll() {
    }

    @Test
    @DisplayName("테스트 05. 카테고리 삭제 테스트")
    void _05_delete() {
    }
}