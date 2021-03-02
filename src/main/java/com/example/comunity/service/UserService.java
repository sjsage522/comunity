package com.example.comunity.service;

import com.example.comunity.domain.User;
import com.example.comunity.dto.user.UserDeleteDto;
import com.example.comunity.dto.user.UserJoinDto;
import com.example.comunity.dto.user.UserUpdateDto;
import com.example.comunity.exception.DuplicateUserIdException;
import com.example.comunity.exception.DuplicateUserNickNameException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public String join(final UserJoinDto userJoinDto) {
        User findUserById = userRepository.findUserById(userJoinDto.getUserId());
        if (findUserById != null) {
            if (findUserById.getUserId().equals(userJoinDto.getUserId())) {
                throw new DuplicateUserIdException("이미 존재하는 아이디 입니다.");
            }
        }
        User findUserByNickName = userRepository.findUserByNickName(userJoinDto.getNickName());
        if (findUserByNickName != null) {
            if (findUserByNickName.getNickName().equals(userJoinDto.getNickName())) {
                throw new DuplicateUserNickNameException("이미 존재하는 별명 입니다.");
            }
        }

        User newUser = User.createUser(
                userJoinDto.getUserId(),
                userJoinDto.getName(),
                userJoinDto.getNickName(),
                userJoinDto.getPassword(),
                userJoinDto.getEmail()
        );


        return userRepository.join(newUser);
    }

    @Transactional
    public User update(final String id, final UserUpdateDto userUpdateDto) {
        User findUser = userRepository.findUserById(id);

        findUser.changeName(userUpdateDto.getName());
        findUser.changeNickname(userUpdateDto.getNickName());
        findUser.changePassword(userUpdateDto.getPassword());


        return findUser;
    }

    @Transactional
    public int delete(final String id, final UserDeleteDto userDeleteDto) {
        User findUser = findById(id);

        if (findUser.getUserId().equals(userDeleteDto.getUserId()) &&
                findUser.getPassword().equals(userDeleteDto.getPassword())) {
            return userRepository.delete(id);
        }
        return 0;
    }

    public User findById(final String userId) {
        User findUser = userRepository.findUserById(userId);
        if (findUser == null) throw new NoMatchUserInfoException("존재하지 않는 사용자 입니다.");
        return findUser;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
