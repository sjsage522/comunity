package com.example.comunity.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("도메인 테스트 (user)")
class UserTest {

    @Test
    @DisplayName("사용자 객체 생성 성공 테스트")
    void create_user_succeed_test() {
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");

        assertThat(user.getUserId()).isEqualTo("testId");
        assertThat(user.getName()).isEqualTo("tester");
    }

    @Test
    @DisplayName("사용자 객체 생성 실패 테스트")
    void create_user_failed_test() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> User.of(null, "name", "nickname", "password", "email")),
                () -> assertThrows(IllegalArgumentException.class, () -> User.of("", "name", "nickname", "password", "email")),
                () -> assertThrows(IllegalArgumentException.class, () -> User.of(" ", "name", "nickname", "password", "email")),
                () -> assertThrows(IllegalArgumentException.class, () -> User.of("userId", null, "nickname", "password", "email"))
        );
    }
}

