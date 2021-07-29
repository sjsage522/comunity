package com.example.comunity.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("도메인 테스트 (category)")
class CategoryTest {

    @Test
    @DisplayName("[성공 테스트] 카테고리 객체 생성")
    void create_category_succeed_test() {
        Category category1 = Category.from("game");
        Category category2 = Category.from("coding");

        assertThat(category1.getCategoryName().getKr()).isEqualTo("게임");
        assertThat(category2.getCategoryName().getKr()).isEqualTo("코딩");
    }

    @Test
    @DisplayName("[실패 테스트] 카테고리 객체 생성")
    void create_category_failed_test() {

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> Category.from(""), "카테고리 이름은 빈 문자열이면 안됨"),
                () -> assertThrows(IllegalArgumentException.class, () -> Category.from(null), "카테고리 이름은 null 이면 안됨"),
                () -> assertThrows(IllegalArgumentException.class, () -> Category.from("qwer"), "존재하는 카테고리 이름이 아님")
        );
    }
}


