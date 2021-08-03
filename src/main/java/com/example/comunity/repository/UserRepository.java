package com.example.comunity.repository;

import com.example.comunity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(final String userId);

    Optional<User> findByUserIdOrNickName(final String userId, final String nickName);
}
