package com.example.comunity.controller.api;

import com.example.comunity.domain.User;
import com.example.comunity.dto.api.ApiResult;
import com.example.comunity.dto.user.UserJoinRequest;
import com.example.comunity.dto.user.UserLoginRequest;
import com.example.comunity.dto.user.UserResponse;
import com.example.comunity.dto.user.UserUpdateRequest;
import com.example.comunity.security.Auth;
import com.example.comunity.security.AuthUser;
import com.example.comunity.service.UserAuthService;
import com.example.comunity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;

import static com.example.comunity.dto.api.ApiResult.succeed;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;

    /**
     * 회원가입
     *
     * @param userJoinRequest 회원가입 dto
     */
    @PostMapping("/join")
    public ResponseEntity<ApiResult<UserResponse>> join(
            final @Valid @RequestBody UserJoinRequest userJoinRequest) {
        final User newUser = userService.join(userJoinRequest);

        return ResponseEntity
                .created(URI.create("/users/" + newUser.getUserId()))
                .body(succeed(getUserResponse(newUser)));
    }

    /**
     * 로그인
     *
     * @param userLoginRequest 로그인 dto
     * @param session          서버 세션
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResult<UserResponse>> login(
            final @Valid @RequestBody UserLoginRequest userLoginRequest,
            final HttpSession session) {
        final User loginUser = userAuthService.authenticate(userLoginRequest.getUserId(), userLoginRequest.getPassword());
        session.setAttribute("authInfo", loginUser);

        log.info("login user = {}", loginUser);
        return ResponseEntity
                .ok(succeed(getUserResponse(loginUser)));
    }

    /**
     * 로그아웃
     *
     * @param session 서버 세션
     */
    @Auth
    @PostMapping("/logout")
    public ResponseEntity<ApiResult<String>> logout(
            final HttpSession session) {
        log.info("logout user = {}", session.getAttribute("authInfo"));
        session.invalidate();

        return ResponseEntity
                .ok(succeed("logout succeed"));
    }

    /**
     * 사용자 단건 조회
     *
     * @param userId 조회할 아이디
     */
    @Auth
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResult<UserResponse>> findById(
            final @PathVariable String userId) {
        final User findUser = userService.findById(userId);

        return ResponseEntity
                .ok(succeed(getUserResponse(findUser)));
    }

    /**
     * 사용자 정보 수정
     *
     * @param userId            사용자 아이디
     * @param userUpdateRequest 사용자 정보 수정 dto
     * @param loginUser         현재 로그인한 사용자
     */
    @Auth
    @PatchMapping("/users/{userId}")
    public ResponseEntity<ApiResult<UserResponse>> update(
            final @PathVariable String userId,
            final @Valid @RequestBody UserUpdateRequest userUpdateRequest,
            final @AuthUser User loginUser) {
        final User updatedUser = userService.update(userId, userUpdateRequest, loginUser);

        return ResponseEntity
                .ok(succeed(getUserResponse(updatedUser)));
    }

    /**
     * 사용자 정보 삭제
     *
     * @param userId  사용자 아이디
     * @param session 서버 세션
     */
    @Auth
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResult<String>> delete(
            final @PathVariable String userId,
            final HttpSession session) {
        final User loginUser = (User) session.getAttribute("authInfo");
        userService.delete(userId, loginUser);

        session.invalidate();

        return ResponseEntity
                .ok(succeed(loginUser.getUserId() + ": user is deleted successfully"));
    }

    private UserResponse getUserResponse(User newUser) {
        return new UserResponse(newUser);
    }
}
