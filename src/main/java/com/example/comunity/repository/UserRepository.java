package com.example.comunity.repository;

import com.example.comunity.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    /**
     * @param user 가입할 사용자
     * @return 회원가입한 사용자 id
     */
    public User join(final User user) {
        em.persist(user);
        em.flush();
        return user;
    }

    /**
     * 사용자 탈퇴 기능
     *
     * @param userId 탈퇴할 사용자 Id
     * @return 탈퇴한 사용자 id
     */
    public int delete(final String userId) {
        return em.createQuery(
                "delete from User u" +
                        " where u.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    /**
     * @return User 개체들을 리스트에 담아서 반환
     */
    public List<User> findAll() {
        return em.createQuery("select u from User u", User.class)
                .getResultList();
    }

    /**
     * @return 해당 id를 갖는 사용자 반환
     */
    public User findUserById(final String userId) {
        try {
            return em.createQuery("select u from User u where u.userId = :userId", User.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            String message = nre.getMessage();
            System.out.println("nre(message) = " + message);
            return null;
        }
    }

    public User findUserByNickName(final String nickName) {
        try {
            return em.createQuery("select u from User u where u.nickName = :nickName", User.class)
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            String message = nre.getMessage();
            System.out.println("nre(message) = " + message);
            return null;
        }
    }

    public User findUserByIdWithPassword(final String userId, final String password) {
        try {
            return em.createQuery(
                    "select u from User u" +
                            " where u.userId = :userId" +
                            " and u.password = :password", User.class)
                    .setParameter("userId", userId)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException nre) {
            String message = nre.getMessage();
            System.out.println("nre(message) = " + message);
            return null;
        }
    }
}
