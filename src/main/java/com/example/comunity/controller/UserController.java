package com.example.comunity.controller;

import com.example.comunity.domain.User;
import com.example.comunity.dto.UserJoinDto;
import com.example.comunity.dto.UserLoginDto;
import com.example.comunity.exception.DuplicateUserIdException;
import com.example.comunity.exception.DuplicateUserNickNameException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.service.UserAuthService;
import com.example.comunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;

    @PostMapping("/users")
    public ResponseEntity<UserJoinDto> join(@Valid @RequestBody final UserJoinDto userJoinDto)
            throws DuplicateUserIdException, DuplicateUserNickNameException {

        userService.join(userJoinDto);

        return new ResponseEntity<>(userJoinDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginDto> login(@Valid @RequestBody final UserLoginDto userLoginDto, final HttpSession session)
            throws NoMatchUserInfoException {
        User loginUser = userAuthService.authenticate(userLoginDto.getUserId(), userLoginDto.getPassword());

        session.setAttribute("authInfo", loginUser);
        userLoginDto.setNickName(loginUser.getNickName());

        return new ResponseEntity<>(userLoginDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/logout")
    public void logout(final HttpSession session) {
        session.invalidate();
    }
}
