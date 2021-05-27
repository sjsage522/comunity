package com.example.comunity.service;

import com.example.comunity.domain.User;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;

    public User authenticate(final String userId, final String password) {
        return userRepository.findByUserIdAndPassword(userId, password)
                .orElseThrow(() -> new NoMatchUserInfoException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }
}
