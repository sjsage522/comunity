package com.example.comunity.repository;

import com.example.comunity.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
//    @Qualifier("userRepositoryImpl")
    UserRepository userRepository;

    @Test
    void join() {

        User newUser = User.createUser("junseok1234", "junseok", "jun", "1234", "test@gmail.com");
        User savedUser = userRepository.join(newUser);
        System.out.println("newUser = " + newUser);
        System.out.println("savedUser = " + savedUser);
        Assertions.assertThat(savedUser.getUserId()).isEqualTo("junseok1234");
    }

    @Test
    void delete() {

        User newUser = User.createUser("junseok1234", "junseok", "jun", "1234", "test@gmail.com");
        User savedUser = userRepository.join(newUser);
        Assertions.assertThat(savedUser.getUserId()).isEqualTo("junseok1234");

        userRepository.delete("junseok1234");
        User findUser = userRepository.findUserById("junseok1234"); /* Optional 처리 해줘야함, 서비스 계층 로직 수정 예정 (검증 부분 등..) */
        Assertions.assertThat(findUser).isNull();
    }

    @Test
    void findAll() {
    }

    @Test
    void findUserById() {
    }

    @Test
    void findUserByNickName() {
    }

    @Test
    void findUserByIdWithPassword() {
    }
}