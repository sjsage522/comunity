package com.example.comunity.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("도메인 테스트 (Category)")
class CategoryTest {

    @Test
    @DisplayName("카테고리 생성 테스트")
    void create_category_test() {

        assertAll(
                () -> Category.of("reading"),
                () -> assertThrows(IllegalArgumentException.class, () -> Category.of(""))
        );
    }
}