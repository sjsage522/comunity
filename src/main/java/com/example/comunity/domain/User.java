package com.example.comunity.domain;

import com.example.comunity.dto.user.UserJoinRequest;
import com.example.comunity.exception.NoMatchUserInfoException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;

import java.util.Arrays;
import java.util.Objects;

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

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickName", unique = true, nullable = false)
    private String nickName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private User(final String userId, final String name, final String nickName, final String password, final String email) {
        validationCheck(userId, name, nickName, password, email);
        this.userId = userId;
        this.name = name;
        this.nickName = nickName;
        this.password = passwordEncoding(password);
        this.email = email;
    }

    // 패스워드 인코딩
    private String passwordEncoding(final String password) {
        return Base64.encodeBase64String(DigestUtils.sha512(password));
    }

    /* 생성자 팩토리 메서드 */
    public static User from(final UserJoinRequest source) {
        return new User(source.getUserId(), source.getName(), source.getNickName(), source.getPassword(), source.getEmail());
    }

    public static User of(final String userId, final String name, final String nickName, final String password, final String email) {
        return new User(userId, name, nickName, password, email);
    }
    /*                */

    /* 변경을 위한 추가 메서드 (사용자 정보 수정) */
    public void updateUserInfo(final String name, final String nickName, final String password) {
        this.name = (name == null || name.isBlank() ? this.name : name);
        this.nickName = (nickName == null || nickName.isBlank() ? this.nickName : nickName);
        this.password = (password == null || password.isBlank() ? this.password : passwordEncoding(password));
    }
    /*                                 */

    // 사용자 로그인 내부 로직
    public void login(final String password) {
        if (password.isBlank() || !passwordEncoding(password).equals(this.password)) {
            throw new NoMatchUserInfoException("비밀번호가 일치하지 않습니다.");
        }
    }

    public String getPassword() {
        return "[PROTECTED]";
    }

    // 생성자 매개변수 유효성 검사 메서드
    private void validationCheck(final String... values) {
        Arrays.stream(values)
                .filter(value -> value == null || value.isBlank())
                .forEach(value -> {
                    throw new IllegalArgumentException("유요하지 않은 매개변수입니다.");
                });
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getUserId().equals(user.getUserId()) && getPassword().equals(user.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getPassword());
    }
}
