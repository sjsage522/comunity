package com.example.comunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "Users")
@SequenceGenerator(
        name = "user_sequence_generator",
        sequenceName = "user_sequence"
)
public class User {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence_generator"
    )
    private Long id;

    @Column(unique = true)
    private String userId;

    private String name;

    @Column(unique = true)
    private String nickName;
    private String password;
    private String email;

    private User(final String userId, final String name, final String nickName, final String password, final String email) {
        this.userId = userId;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.email = email;
    }

    public static User createUser(final String userId, final String name, final String nickName, final String password, final String email) {
        return new User(userId, name, nickName, password, email);
    }
}
