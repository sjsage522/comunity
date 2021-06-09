package com.example.comunity.repository;

import com.example.comunity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    int deleteByUserId(String userId);

    Optional<User> findByUserId(String userId);

    Optional<User> findByNickName(String nickName);

    Optional<User> findByUserIdOrNickName(String userId, String nickName);
}
