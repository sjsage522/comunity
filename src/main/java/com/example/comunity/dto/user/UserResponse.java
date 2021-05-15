package com.example.comunity.dto.user;

import com.example.comunity.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("nickname")
    private String nickName;

    private String email;
    private String name;

    @JsonProperty("created_at")
    private LocalDateTime createdDate;

    public UserResponse(final User newUser) {
        this.userId = newUser.getUserId();
        this.nickName = newUser.getNickName();
        this.email = newUser.getEmail();
        this.name = newUser.getName();
        this.createdDate = newUser.getCreatedDate();
    }
}
