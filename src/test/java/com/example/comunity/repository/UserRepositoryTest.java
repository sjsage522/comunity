package com.example.comunity.repository;

import com.example.comunity.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("회원 가입 테스트")
    void joinUserTest() {
        //given
        User user = User.createUser(
                "junseok1234",
                "junseok",
                "jun",
                "1234",
                "junseok@example.com",
                "010-0000-0000");

        //when
        String userId = userRepository.join(user);

        //then
        assertThat(userId).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("회원 삭제 메소드 테스트")
    void deleteUserByIdTest() {
        //given
        User user = User.createUser(
                "junseok1234",
                "junseok",
                "jun",
                "1234",
                "junseok@example.com",
                "010-0000-0000");

        String userId = userRepository.join(user);

        //when
        String deletedUserId = userRepository.deleteUserById(userId);

        //then
        Assertions.assertThat(userRepository.findUserById(deletedUserId)).isNull();
    }
}