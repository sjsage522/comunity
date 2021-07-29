package com.example.comunity.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("도메인 테스트 (board)")
class BoardTest {

    @Test
    @DisplayName("[성공 테스트] board 객체 생성")
    void create_board_succeed_test() {
        User user = User.of("testId", "tester", "testNick", "1234", "tester@gmail.com");
        Category category = Category.from("game");

        Board board = Board.of(user, category, "title", "content");

        assertThat(board.getUser().getUserId()).isEqualTo("testId");
        assertThat(board.getCategory().getCategoryName().getEn()).isEqualTo("GAME");
        assertThat(board.getTitle()).isEqualTo("title");
        assertThat(board.getContent()).isEqualTo("content");
    }


    @Test
    @DisplayName("[실패 테스트] board 객체 생성")
    void create_board_failed_test() {
        User user = User.of("testId", "tester", "testNick", "1234", "tester@gmail.com");
        Category category = Category.from("game");

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> Board.of(null, category, "title", "content"), "user는 null이면 안됨"),
                () -> assertThrows(IllegalArgumentException.class, () -> Board.of(user, null, "title", "content"), "category는 null이면 안됨"),
                () -> assertThrows(IllegalArgumentException.class, () -> Board.of(user, category, null, "content"), "title은 null이면 안됨"),
                () -> assertThrows(IllegalArgumentException.class, () -> Board.of(user, category, "title", null), "content는 null이면 안됨"),
                () -> assertThrows(IllegalArgumentException.class, () -> Board.of(user, category, "", "content"), "title은 빈문자열이면 안됨"),
                () -> assertThrows(IllegalArgumentException.class, () -> Board.of(user, category, "title", ""), "content는 빈문자열이면 안됨")
        );
    }
}

