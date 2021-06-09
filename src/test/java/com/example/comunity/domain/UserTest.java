package com.example.comunity.domain;

import com.example.comunity.exception.NoMatchUserInfoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("도메인 테스트 (User)")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
class UserTest {

    User user;

    @Test
    @DisplayName("01. 사용자 생성 테스트")
    void _01_create_user_test() {

        user = User.from(
                "testId",
                "tester",
                "TT",
                "1234",
                "tester@gmail.com"
        );

        assertAll(
                () -> assertThat(user.getUserId()).isEqualTo("testId"),
                () -> assertThat(user.getName()).isEqualTo("tester"),
                () -> assertThat(user.getNickName()).isEqualTo("TT"),
                () -> assertThat(user.getPassword()).isEqualTo(Base64.encodeBase64String(DigestUtils.sha512("1234"))),
                () -> assertThat(user.getEmail()).isEqualTo("tester@gmail.com")
        );
    }

    @Test
    @DisplayName("02. 사용자 로그인 테스트")
    void _02_login_user_test() {

        assertAll(
                () -> user.login("1234"),
                () -> assertThrows(NoMatchUserInfoException.class, () -> user.login("")),
                () -> assertThrows(NoMatchUserInfoException.class, () -> user.login("1111"))
        );
    }
}