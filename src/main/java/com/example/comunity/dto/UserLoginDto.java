package com.example.comunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {

    private String userId;

    private String pw;

    private String nickName;

    public UserLoginDto(String userId, String nickName) {
        this.userId = userId;
        this.nickName = nickName;
    }
}
