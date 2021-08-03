package com.example.comunity.dto.user;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Objects;

@Getter
public class UserJoinRequest {

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

    protected UserJoinRequest() {}

    public static class Builder {
        /* 필수 매개 변수 */
        private String userId;
        private String name;
        private String nickName;
        private String password;
        private String email;
        /*            */

        public Builder userId(final String userId) {
            this.userId = userId;
            return this;
        }

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

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public UserJoinRequest build() {
            return new UserJoinRequest(this);
        }
    }

    private UserJoinRequest(final Builder builder) {
        checkValid(builder.userId, builder.name, builder.nickName, builder.password, builder.email);
        this.userId = builder.userId;
        this.name = builder.name;
        this.nickName = builder.nickName;
        this.password = builder.password;
        this.email = builder.email;
    }

//    private UserJoinRequest(String userId, String name, String nickName, String password, String email) {
//        checkValid(userId, name, nickName, password, email);
//        this.userId = userId;
//        this.name = name;
//        this.nickName = nickName;
//        this.password = password;
//        this.email = email;
//    }
//
//    public static UserJoinRequest from(String userId, String name, String nickName, String password, String email) {
//        return new UserJoinRequest(userId, name, nickName, password, email);
//    }

    private void checkValid(final String... args) {
        Arrays.stream(args)
                .filter(Objects::isNull)
                .forEach(arg -> {
                    throw new IllegalArgumentException("user join dto [arguments] must be not null.");
                });
    }
}
