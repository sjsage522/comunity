package com.example.comunity.repository;

import com.example.comunity.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("모든 사용자 조회 테스트")
    void findUserTest() {
        //given
        List<User> users = userRepository.findAll();

        //when

        //then
//        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("모든 사용자 조회 테스트2")
    void findUserTest2() {
        //given
        User user1 = getUser("junseok1234", "junseok", "junEE", "1234", "junseok@example.com");
        User user2 = getUser("person551", "person", "pp551", "1234", "person551@example.com");
        User user3 = getUser("person552", "person", "pp552", "1234", "person551@example.com");
        User user4 = getUser("person553", "person", "pp553", "1234", "person553@example.com");
        User user5 = getUser("person554", "person", "pp554", "1234", "person553@example.com");

        userRepository.join(user1);
        userRepository.join(user2);
        userRepository.join(user3);
        userRepository.join(user4);
        userRepository.join(user5);

        List<User> users = userRepository.findAll();

        //when

        //then
//        assertThat(users.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("회원 가입 테스트")
    void joinUserTest() {
        //given
        User user1 = getUser("junseok1234", "junseok", "junEE", "1234", "junseok@example.com");

        //when
        String userId = userRepository.join(user1);

        em.flush();
        em.clear();

        //then
        assertThat(userId).isEqualTo(user1.getUserId());
    }

    @Test
    @DisplayName("회원 삭제 메소드 테스트")
    void deleteUserByIdTest() {
        //given
        User user = getUser("junseok1234", "junseok", "junEE", "1234", "junseok@example.com");

        String userId = userRepository.join(user);

        //when
        int count = userRepository.delete(userId);

        //then
        Assertions.assertThat(count).isEqualTo(1);
    }

    private User getUser(String userId, String name, String nickName, String password, String email) {
        return User.createUser(
                userId,
                name,
                nickName,
                password,
                email);
    }
}