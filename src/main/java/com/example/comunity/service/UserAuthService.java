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

    public User authenticate(String userId, String password) {
        User findUser = userRepository.findUserByIdWithPassword(userId, password);

        if (findUser == null) {
            throw new NoMatchUserInfoException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return findUser;
    }
}
