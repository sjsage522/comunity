package com.example.comunity.dto.user;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class UserLoginRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
