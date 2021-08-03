package com.example.comunity.dto.user;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class UserUpdateRequest {

    private String name;

    private String nickName;

    private String password;

    protected UserUpdateRequest() {}

    /* 빌더를 직접 구현해보자 */
    public static class Builder {
        /* 선택 매개변수 */
        private String name;
        private String nickName;
        private String password;
        /*           */

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder nickName(final String nickName) {
            this.nickName = nickName;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public UserUpdateRequest build() {
            return new UserUpdateRequest(this);
        }
    }

    private UserUpdateRequest(final Builder builder) {
        name = builder.name;
        nickName = builder.nickName;
        password = builder.password;
    }
}
