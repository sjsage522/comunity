package com.example.comunity.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("도메인 테스트 (Comment)")
class CommentTest {

    static User user;
    static Category category;
    static Board board;

    @BeforeAll
    static void init() {
        user = User.of("testId", "tester", "tester", "1234", "tester@gmail.com");
        category = Category.of("reading");
        board = Board.from(user, category, "test_title", "test_content");
    }

    @Test
    @DisplayName("도메인 생성 테스트")
    void create_comment_test() {

        Comment valid = Comment.from(user, board, "test_content");

        assertAll(
                () -> {
                    assertThat(user.getName()).isEqualTo("tester");
                    assertThat(board.getTitle()).isEqualTo("test_title");
                    assertThat(valid.getContent()).isEqualTo("test_content");
                },
                () -> assertThrows(IllegalArgumentException.class, () -> Comment.from(null, board, "test_content")),
                () -> assertThrows(IllegalArgumentException.class, () -> Comment.from(user, null, "test_content")),
                () -> assertThrows(IllegalArgumentException.class, () -> Comment.from(user, board, "")),
                () -> assertThrows(IllegalArgumentException.class, () -> Comment.from(user, board, null))
        );
    }
}