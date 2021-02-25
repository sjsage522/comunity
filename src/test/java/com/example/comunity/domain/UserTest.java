package com.example.comunity.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("사용자 생성 테스트")
    void createUserTest() {
        //given
        User user = User.createUser(
                "junseok1234",
                "junseok",
                "jun",
                "1234",
                "junseok@example.com");

        //when
        //then
        assertThat(user.getUserId()).isEqualTo("junseok1234");
        assertThat(user.getName()).isEqualTo("junseok");
        assertThat(user.getNickName()).isEqualTo("jun");
        assertThat(user.getPassword()).isEqualTo("1234");
        assertThat(user.getEmail()).isEqualTo("junseok@example.com");
    }
}