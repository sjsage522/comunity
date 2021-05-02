package com.example.comunity.repository;

import com.example.comunity.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.MethodName.class)
@DisplayName("사용자 레포지토리 테스트")
class UserRepositoryTest {

    @Autowired
//    @Qualifier("userRepositoryImpl")
    UserRepository userRepository;

    @Test
    @DisplayName("테스트 01. 사용자 회원 가입 테스트")
    void _01_join() {

        User newUser = getUser("junseok1234", "junseok", "jun", "1234", "test@gmail.com");
        User savedUser = userRepository.join(newUser);
        System.out.println("newUser = " + newUser);
        System.out.println("savedUser = " + savedUser);
        assertThat(savedUser.getUserId()).isEqualTo("junseok1234");
    }

    private User getUser(String userId, String name, String nickName, String password, String email) {
        return User.createUser(userId, name, nickName, password, email);
    }

    @Test
    @DisplayName("테스트 02. 사용자 삭제 테스트")
    void _02_delete() {

        User newUser = getUser("junseok1234", "junseok", "jun", "1234", "test@gmail.com");
        User savedUser = userRepository.join(newUser);
        assertThat(savedUser.getUserId()).isEqualTo("junseok1234");

        userRepository.delete("junseok1234");
        User findUser = userRepository.findUserById("junseok1234"); /* Optional 처리 해줘야함, 서비스 계층 로직 수정 예정 (검증 부분 등..) */
        assertThat(findUser).isNull();
    }

    @Test
    @DisplayName("테스트 03. 모든 사용자 조회 테스트")
    void _03_findAll() {

        //given
        User newUser1 = getUser("junseok1234", "junseok", "jun", "1234", "test@gmail.com");
        User newUser2 = getUser("tester", "hong", "test", "1234", "hong@gmail.com");

        userRepository.join(newUser1);
        userRepository.join(newUser2);

        //when
        List<User> users = userRepository.findAll();

        //then
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("테스트 04. 사용자 조회 테스트 (by userId)")
    void _04_findUserById() {

        //given
        User newUser = getUser("junseok1234", "junseok", "jun", "1234", "test@gmail.com");
        userRepository.join(newUser);

        //when
        User findUser = userRepository.findUserById("junseok1234");

        //then
        assertThat(findUser.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    @DisplayName("테스트 05. 사용자 조회 테스트 (by nickName)")
    void _05_findUserByNickName() {

        //given
        User newUser = getUser("junseok1234", "junseok", "jun", "1234", "test@gmail.com");
        userRepository.join(newUser);

        //when
        User findUser = userRepository.findUserByNickName("jun");

        //then
        assertThat(findUser.getUserId()).isEqualTo("junseok1234");
    }

    @Test
    @DisplayName("테스트 06. 사용자 조회 테스트 (by userId and password)")
    void _06_findUserByIdWithPassword() {

        //given
        User newUser = getUser("junseok1234", "junseok", "jun", "1234", "test@gmail.com");
        userRepository.join(newUser);

        //when
        User findUser = userRepository.findUserByIdWithPassword("junseok1234", "1234");

        //then
        assertThat(findUser.getNickName()).isEqualTo("jun");
    }
}