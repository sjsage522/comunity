package com.example.comunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinDto {

    @NotBlank(message = "아이디는 필수입니다.")
    private String userId;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "별명은 필수입니다.")
    private String nickName;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
}
