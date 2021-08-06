package com.example.comunity.controller.api;

import com.example.comunity.domain.User;
import com.example.comunity.dto.api.ApiResult;
import com.example.comunity.dto.user.UserResponse;
import com.example.comunity.security.Auth;
import com.example.comunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.comunity.dto.api.ApiResult.succeed;

/**
 * 관리자용 API
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Auth(role = Auth.Role.ADMIN)
public class AdminController {

    private final UserService userService;

    /**
     * 모든 사용자 조회
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResult<List<UserResponse>>> findAll() {
        final List<UserResponse> userResponseList = userService
                .findAll()
                .stream()
                .map(this::getUserResponse)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(succeed(userResponseList));
    }

    private UserResponse getUserResponse(User newUser) {
        return new UserResponse(newUser);
    }
}
