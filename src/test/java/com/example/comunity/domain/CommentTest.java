package com.example.comunity.domain;

import com.example.comunity.dto.user.UserJoinRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("도메인 테스트 (comment)")
class CommentTest {

    @Test
    @DisplayName("[성공 테스트] 답글 객체 생성")
    void create_comment_succeed_test() {
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category category = Category.from("game");
        Board board = Board.of(user, category, "testerTitle", "content");

        Comment comment = Comment.of(user, board, "commentContent");

        assertThat(comment.getBoard().getTitle()).isEqualTo("testerTitle");
        assertThat(comment.getUser().getUserId()).isEqualTo("testId");
        assertThat(comment.getContent()).isEqualTo("commentContent");
    }

    @Test
    @DisplayName("[실패 테스트] 답글 객체 생성")
    void create_comment_failed_test() {
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category category = Category.from("game");
        Board board = Board.of(user, category, "testerTitle", "content");

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> Comment.of(null, board, "content")),
                () -> assertThrows(IllegalArgumentException.class, () -> Comment.of(user, null, "content")),
                () -> assertThrows(IllegalArgumentException.class, () -> Comment.of(user, board, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> Comment.of(user, board, "")),
                () -> assertThrows(IllegalArgumentException.class, () -> Comment.of(user, board, " "))
        );
    }
}
