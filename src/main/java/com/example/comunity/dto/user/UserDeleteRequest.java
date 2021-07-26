package com.example.comunity.dto.user;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class UserDeleteRequest {

    @NotBlank(message = "삭제할 사용자의 아이디를 입력해주세요.")
    String userId;
    @NotBlank(message = "삭제할 사용자의 비밀번호를 입력해주세요.")
    String password;
}
