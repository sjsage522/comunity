package com.example.comunity.service;

import com.example.comunity.domain.User;
import com.example.comunity.dto.user.UserJoinDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    @DisplayName("회원가입 테스트")
    void userJoinTest() {
        //given
        User user = getUser("junseok1234", "junseok", "junEE", "1234", "junseok@example.com");

        //when
        User joinUser = userService.join(new UserJoinDto(user.getUserId(), user.getName(), user.getNickName(), user.getPassword(), user.getEmail()));

        //then
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