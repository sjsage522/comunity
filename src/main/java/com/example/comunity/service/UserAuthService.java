package com.example.comunity.service;

import com.example.comunity.domain.User;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User authenticate(
            final String userId,
            final String password) {

        return findByUserId(userId)
                .map(user -> {
                    user.login(password);
                    return user;
                })
                .orElseThrow(() -> new NoMatchUserInfoException("아이디가 일치하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUserId(final String userId) {
        return userRepository.findByUserId(userId);
    }
}
