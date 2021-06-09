package com.example.comunity.domain;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("도메인 테스트 (Board)")
class BoardTest {

    static User user;
    static Category category;

    @BeforeAll
    static void init() {
        user = User.from("testId", "tester", "tester", "1234", "tester@gmail.com");
        category = Category.of("reading");
    }

    @Test
    @DisplayName("게시글 생성 테스트")
    void create_board_test() {

        Board board = Board.from(user, category, "test_title", "test_content");

        assertAll(
                () -> assertThat(board.getUser().getUserId()).isEqualTo("testId"),
                () -> assertThat(board.getUser().getName()).isEqualTo("tester"),
                () -> assertThat(board.getUser().getNickName()).isEqualTo("tester"),
                () -> assertThat(board.getUser().getPassword()).isEqualTo(Base64.encodeBase64String(DigestUtils.sha512("1234"))),
                () -> assertThat(board.getUser().getEmail()).isEqualTo("tester@gmail.com"),
                () -> assertThat(board.getCategory().getCategoryName()).isEqualTo("reading"),
                () -> assertThat(board.getTitle()).isEqualTo("test_title"),
                () -> assertThat(board.getContent()).isEqualTo("test_content"),
                () -> assertThrows(IllegalArgumentException.class, () -> Board.from(null, category, "error_title", "error_content")),
                () -> assertThrows(IllegalArgumentException.class, () -> Board.from(user, null, "error_title", "error_content")),
                () -> assertThrows(IllegalArgumentException.class, () -> Board.from(null, null, "error_title", "error_content"))
        );
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void update_board_test() {

        Board board = Board.from(user, category, "test_title", "test_content");
        Category newCategory = Category.of("new_category");

        board.changeBoard("update_title", "update_content", newCategory);

        assertAll(
                () -> assertThat(board.getTitle()).isEqualTo("update_title"),
                () -> assertThat(board.getContent()).isEqualTo("update_content"),
                () -> assertThat(board.getCategory().getCategoryName()).isEqualTo("new_category"),
                () -> assertThrows(IllegalArgumentException.class, () -> board.changeBoard(null, "update_content", newCategory)),
                () -> assertThrows(IllegalArgumentException.class, () -> board.changeBoard("update_title", null, newCategory)),
                () -> assertThrows(IllegalArgumentException.class, () -> board.changeBoard("update_title", "update_content", null))
        );
    }
}