package com.example.comunity.service;

import com.example.comunity.domain.User;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;

    public User authenticate(final String userId, final String password) {

        return findByUserId(userId)
                .map(user -> {
                    user.login(password);
                    return user;
                })
                .orElseThrow(() -> new NoMatchUserInfoException("아이디가 일치하지 않습니다."));
    }

    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
}
