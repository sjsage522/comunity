package com.example.comunity.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String userId;
    private String name;
    private String nickName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String email;

    public UserDto(String userId, String name, String nickName, String email) {
        this.userId = userId;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
    }
}
