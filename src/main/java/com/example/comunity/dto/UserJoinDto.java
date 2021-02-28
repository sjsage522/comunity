package com.example.comunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinDto {

    private String userId;

    private String name;

    private String nickName;

    private String password;

    private String email;
}
