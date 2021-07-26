package com.example.comunity.controller;

import com.example.comunity.domain.User;
import com.example.comunity.dto.api.ApiResult;
import com.example.comunity.dto.user.*;
import com.example.comunity.exception.DuplicateUserIdException;
import com.example.comunity.exception.DuplicateUserNickNameException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.service.UserAuthService;
import com.example.comunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.comunity.dto.api.ApiResult.succeed;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;

    /**
     * 회원가입
     * @param userJoinRequest 회원가입 dto
     * @throws DuplicateUserIdException 이미 존재하는 id가 있는지 검사
     * @throws DuplicateUserNickNameException 이미 존재하는 별명이 있는지 검사
     */
    @PostMapping("/join")
    public ResponseEntity<ApiResult<UserResponse>> join(@Valid @RequestBody final UserJoinRequest userJoinRequest)
            throws DuplicateUserIdException, DuplicateUserNickNameException {

        User newUser = userService.join(userJoinRequest);

        return ResponseEntity
                .created(URI.create("/users/" + newUser.getUserId()))
                .body(succeed(getUserResponse(newUser)));
    }

    /**
     * 로그인
     * @param userLoginRequest 로그인 dto
     * @param session 헌재 사용자 세션
     * @throws NoMatchUserInfoException 아이디와 비밀번호가 유효한지 검사
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResult<UserResponse>> login(@Valid @RequestBody final UserLoginRequest userLoginRequest, final HttpSession session)
            throws NoMatchUserInfoException {

        User loginUser = userAuthService.authenticate(userLoginRequest.getUserId(), userLoginRequest.getPassword());
        session.setAttribute("authInfo", loginUser);

        return ResponseEntity
                .ok(succeed(getUserResponse(loginUser)));
    }

    /**
     * 로그아웃
     * @param session 현재 사용자 세션
     */
    @GetMapping("/logout")
    public ResponseEntity<ApiResult<String>> logout(final HttpSession session) {

        session.invalidate();
        return ResponseEntity
                .ok(succeed("logout succeed"));
    }

    /**
     * 특정 사용자 조회
     * @param userId 조회할 아이디
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResult<UserResponse>> findById(@PathVariable final String userId) {

        User findUser = userService.findById(userId);
        return ResponseEntity
                .ok(succeed(getUserResponse(findUser)));
    }

    /**
     * 모든 사용자 조회
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResult<List<UserResponse>>> findAll() {

        List<UserResponse> userResponseList = userService
                .findAll()
                .stream()
                .map(this::getUserResponse)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(succeed(userResponseList));
    }

    /**
     * 사용자 정보 수정
     * @param userId 사용자 아이디
     * @param userUpdateRequest 사용자 정보 수정 dto
     */
    @PatchMapping("/users/{userId}")
    public ResponseEntity<ApiResult<UserResponse>> update(@PathVariable final String userId, @Valid @RequestBody final UserUpdateRequest userUpdateRequest, final HttpSession session) {

        User loginUser = (User) session.getAttribute("authInfo");
        User updatedUser = userService.update(userId, userUpdateRequest, loginUser);

        return ResponseEntity
                .ok(succeed(getUserResponse(updatedUser)));
    }

    /**
     * It deletes based upon id then returns an HTTP 204 No Content response.
     * @return http 204 response
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResult<String>> delete(@PathVariable final String userId, @Valid @RequestBody final UserDeleteRequest userDeleteRequest, final HttpSession session) {

        User loginUser = (User) session.getAttribute("authInfo");
        userService.delete(userId, userDeleteRequest, loginUser);
        session.invalidate();

        return ResponseEntity
                .ok(succeed(loginUser.getUserId() + ": user is deleted successfully"));
    }

    private UserResponse getUserResponse(User newUser) {
        return new UserResponse(newUser);
    }
}
