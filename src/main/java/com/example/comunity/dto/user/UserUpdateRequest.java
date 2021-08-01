package com.example.comunity.dto.user;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class UserUpdateRequest {

    private String name;

    private String nickName;

    private String password;

    /* 빌더를 직접 구현해보자 */
    public static class Builder {
        /* 선택 매개변수 */
        private String name;
        private String nickName;
        private String password;
        /*           */

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder nickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public UserUpdateRequest build() {
            return new UserUpdateRequest(this);
        }
    }

    private UserUpdateRequest(Builder builder) {
        name = builder.name;
        nickName = builder.nickName;
        password = builder.password;
    }
}
