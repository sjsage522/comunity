package com.example.comunity.service;

import com.example.comunity.domain.User;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@DisplayName("서비스 테스트 (user_auth)")
@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @InjectMocks
    UserAuthService userAuthService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("[성공 테스트] 로그인 인증")
    void authenticate_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");

        //when
        when(userRepository.findByUserId(user.getUserId()))
                .thenReturn(java.util.Optional.of(user));

        User loginUser = userAuthService.authenticate(user.getUserId(), "1234");

        //then
        assertThat(loginUser.getUserId()).isEqualTo("testId");
    }

    @Test
    @DisplayName("[실패 테스트] 로그인 인증 - 비밀번호가 일치하지 않음")
    void authenticate_failed_test1() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");

        //when
        when(userRepository.findByUserId(user.getUserId()))
                .thenReturn(java.util.Optional.of(user));

        //then
        assertThrows(NoMatchUserInfoException.class,
                () -> userAuthService.authenticate(user.getUserId(), "1111"));
    }

    @Test
    @DisplayName("[실패 테스트] 로그인 인증 - 아이디가 일치하지 않음")
    void authenticate_failed_test2() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");

        //when
        when(userRepository.findByUserId("otherUser"))
                .thenThrow(new NoMatchUserInfoException());

        //then
        assertThrows(NoMatchUserInfoException.class,
                () -> userAuthService.authenticate("otherUser", "1111")
        );
    }
}

