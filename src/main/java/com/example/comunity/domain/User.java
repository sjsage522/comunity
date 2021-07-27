package com.example.comunity.domain;

import com.example.comunity.dto.user.UserJoinRequest;
import com.example.comunity.exception.NoMatchUserInfoException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
@SequenceGenerator(
        name = "user_sequence_generator",
        sequenceName = "user_sequence"
)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(
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
        this.password = passwordEncoding(password);
        this.email = email;
    }

    /**
     * 패스워드 인코딩
     */
    private String passwordEncoding(String password) {
        return Base64.encodeBase64String(DigestUtils.sha512(password));
    }

    public static User from(final UserJoinRequest source) {
        return new User(source.getUserId(), source.getName(), source.getNickName(), source.getPassword(), source.getEmail());
    }

    public static User of(final String userId, final String name, final String nickName, final String password, final String email) {
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
        this.password = passwordEncoding(password);
    }

    public void login(String password) {
        if (password.isBlank() || !passwordEncoding(password).equals(this.password)) {
            throw new NoMatchUserInfoException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + "[PROTECTED]" + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
