package com.example.comunity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteRequest {

    @NotBlank(message = "삭제할 사용자의 아이디를 입력해주세요.")
    String userId;
    @NotBlank(message = "삭제할 사용자의 비밀번호를 입력해주세요.")
    String password;
}
