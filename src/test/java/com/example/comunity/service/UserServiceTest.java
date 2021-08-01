package com.example.comunity.service;

import com.example.comunity.domain.User;
import com.example.comunity.dto.user.UserJoinRequest;
import com.example.comunity.dto.user.UserUpdateRequest;
import com.example.comunity.exception.DuplicateUserIdException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CommentRepository;
import com.example.comunity.repository.FileRepository;
import com.example.comunity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@DisplayName("서비스 테스트 (user)")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    BoardRepository boardRepository;

    @Mock
    FileRepository fileRepository;

    @Mock
    CommentRepository commentRepository;

    User user;

    @BeforeEach
    void userInit() {
        user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
    }

    @Test
    @DisplayName("[성공 테스트] 사용자 회원 가입")
    void join_succeed_test() {
        //given
        UserJoinRequest request = new UserJoinRequest.Builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .name(user.getName())
                .password(user.getPassword())
                .build();

        //when
        when(userRepository.findByUserIdOrNickName(request.getUserId(), request.getNickName()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any()))
                .thenReturn(user);

        User savedUser = userService.join(request);

        //then
        assertThat(savedUser.getUserId()).isEqualTo("testId");
    }

    @Test
    @DisplayName("[실패 테스트] 사용자 회원 가입")
    void join_failed_test() {
        //given
        UserJoinRequest request = new UserJoinRequest.Builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .name(user.getName())
                .password(user.getPassword())
                .build();

        //when
        when(userRepository.findByUserIdOrNickName(request.getUserId(), request.getNickName()))
                .thenReturn(Optional.of(user));

        //then
        assertThrows(DuplicateUserIdException.class, () -> userService.join(request));
        then(userRepository).should(times(0)).save(user);
    }

    @Test
    @DisplayName("[성공 테스트] 사용자 정보 수정 - 이름 변경")
    void update_succeed_test() {
        //given
        UserUpdateRequest request = new UserUpdateRequest
                .Builder()
                .name("UPDATE_NAME")
                .build();

        //when
        when(userRepository.findByUserId(user.getUserId()))
                .thenReturn(Optional.ofNullable(user));
        userService.update(user.getUserId(), request, user);

        //then
        assertThat(user.getUserId()).isEqualTo("testId");
        assertThat(user.getName()).isEqualTo("UPDATE_NAME");
        assertThat(user.getNickName()).isEqualTo("testNickname");
    }

    @Test
    @DisplayName("[실패 테스트] 사용자 정보 수정")
    void update_failed_test() {
        //given
        UserUpdateRequest request = new UserUpdateRequest
                .Builder()
                .name("UPDATE_NAME")
                .build();

        User otherUser = User.of("otherUser", "tester", "otherUser", "1234", "otherUser@gmail.com");

        //when
        when(userRepository.findByUserId(user.getUserId()))
                .thenReturn(Optional.ofNullable(user));

        //then
        assertThrows(NoMatchUserInfoException.class,
                () -> userService.update(user.getUserId(), request, otherUser));
    }

    @Test
    @DisplayName("[성공 테스트] 사용자 정보 삭제")
    void delete_succeed_test() {
        //given
        //when
        when(userRepository.findByUserId(user.getUserId()))
                .thenReturn(Optional.ofNullable(user));
        when(boardRepository.findAllByUser_UserId(user.getUserId()))
                .thenReturn(Collections.emptyList());

        userService.delete(user.getUserId(), user);

        //then
        then(userRepository).should(times(1)).delete(user);
    }

    @Test
    @DisplayName("[실패 테스트] 사용자 성보 삭제")
    void delete_failed_test() {
        //given
        User otherUser = User.of("otherUser", "tester", "otherUser", "1234", "otherUser@gmail.com");

        //when
        when(userRepository.findByUserId(user.getUserId()))
                .thenReturn(Optional.ofNullable(user));

        //then
        assertThrows(NoMatchUserInfoException.class,
                () -> userService.delete(user.getUserId(), otherUser)
        );
        then(boardRepository).should(times(0)).findAllByUser_UserId(user.getUserId());
        then(userRepository).should(times(0)).delete(user);
    }
}
