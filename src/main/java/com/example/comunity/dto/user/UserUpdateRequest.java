package com.example.comunity.dto.user;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class UserUpdateRequest {

    @NotBlank(message = "변경할 이름을 입력해 주세요.")
    private String name;

    @NotBlank(message = "변경할 별명을 입력해 주세요.")
    private String nickName;

    @NotBlank(message = "변경할 비밀번호를 입력해 주세요.")
    private String password;
}
