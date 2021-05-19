package com.example.comunity.domain;

import com.example.comunity.dto.user.UserJoinRequest;
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
public class User extends BaseTimeEntity {

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

    public static User from(final UserJoinRequest source) {
        return new User(source.getUserId(), source.getName(), source.getNickName(), source.getPassword(), source.getEmail());
    }

    public static User from(final String userId, final String name, final String nickName, final String password, final String email) {
        return new User(userId, name, nickName, password, email);
    }

    /**
     * 변경을 위한 추가 메서드 (사용자 정보 수정)
     */
    public void changeName(final String name) {
        this.name = name;
    }

    public void changeNickname(final String nickName) {
        this.nickName = nickName;
    }

    public void changePassword(final String password) {
        this.password = password;
    }
}
