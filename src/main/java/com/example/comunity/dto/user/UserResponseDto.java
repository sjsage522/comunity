package com.example.comunity.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto extends UserDto {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("nickname")
    private String nickName;

    private String email;
    private String name;

    @JsonProperty("created_at")
    private LocalDateTime createdDate;
}
