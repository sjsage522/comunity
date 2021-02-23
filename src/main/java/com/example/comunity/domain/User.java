package com.example.comunity.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "Users")
public class User {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String userId;

    private String name;
    private String nickName;
    private String password;
    private String email;
    private String phoneNumber;

    protected User() {

    }

    public User(String userId, String name, String nickName, String password, String email, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public static User createUser(String userId, String name, String nickName, String password, String email, String phoneNumber) {
        return new User(userId, name, nickName, password, email, phoneNumber);
    }
}
