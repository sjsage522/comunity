package com.example.comunity.service;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Comment;
import com.example.comunity.domain.UploadFile;
import com.example.comunity.domain.User;
import com.example.comunity.dto.user.UserJoinRequest;
import com.example.comunity.dto.user.UserUpdateRequest;
import com.example.comunity.exception.DuplicateUserIdException;
import com.example.comunity.exception.DuplicateUserNickNameException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.FileRepository;
import com.example.comunity.repository.UserRepository;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;

    /**
     * 사용자 아이디와 별명은 unique 하기 때문에 먼저 검사 후 등록되어 있지 않은 사용자라면,
     * User entity 를 생성하고 save
     *
     * @param userJoinRequest user dto
     * @return savedUser
     */
    @Transactional
    public User join(
            final UserJoinRequest userJoinRequest) {
        userRepository.findByUserIdOrNickName(userJoinRequest.getUserId(), userJoinRequest.getNickName())
                .ifPresent(user -> {
                    if (user.getUserId().equals(userJoinRequest.getUserId())) throw new DuplicateUserIdException();
                    else throw new DuplicateUserNickNameException();
                });

        User newUser = User.from(userJoinRequest);
        return userRepository.save(newUser);
    }

    @Transactional
    public User update(
            final String userId,
            final UserUpdateRequest userUpdateRequest,
            final User loginUser) {
        /* 영속상태의 entity */
        User findUser = findById(userId);

        compareUser(loginUser, findUser, "다른 사용자의 정보를 수정할 수 없습니다.");

        /* dirty check */
        findUser.updateUserInfo(
                userUpdateRequest.getName(),
                userUpdateRequest.getNickName(),
                userUpdateRequest.getPassword()
        );
        /* 트랜잭션 커밋시점에 1차캐시의 스냅샷과 영속 상태의 entity 정보와 비교 */
        /* 변경된 부분을 update 쿼리를 통해(영속성 컨텍스트 SQL 저장소) db 데이터를 수정 */

        return findUser;
    }

    /**
     * session 으로 부터 얻어온 사용자와 삭제될 사용자가 일치할 경우에만 삭제 진행
     */
    @Transactional
    public void delete(
            final String userId,
            final User loginUser) {
        User findUser = findById(userId);
        compareUser(loginUser, findUser, "다른 사용자의 정보를 삭제할 수 없습니다.");

        deleteBoards(userId);
        userRepository.delete(findUser);
    }

    @Transactional(readOnly = true)
    public User findById(
            final String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(NoMatchUserInfoException::new);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    private void compareUser(User loginUser, User findUser, String errorMessage) {
        if (!loginUser.getUserId().equals(findUser.getUserId()))
            throw new NoMatchUserInfoException(errorMessage);
    }

    private void deleteBoards(String userId) {
        List<Long> boardIds = boardRepository.findAllByUser_UserId(userId)
                .stream()
                .map(Board::getId)
                .collect(Collectors.toList());

        for (Long boardId : boardIds) {
            deleteComments(boardId);
            deleteFiles(boardId);
        }

        boardRepository.deleteWithIds(boardIds);
    }

    private void deleteComments(Long boardId) {
        List<Long> commentIds = commentRepository.findAllByBoardId(boardId)
                .stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
        commentRepository.deleteWithIds(commentIds);
    }

    private void deleteFiles(Long boardId) {
        List<Long> fileIds = fileRepository.findAllByBoardId(boardId)
                .stream()
                .map(UploadFile::getId)
                .collect(Collectors.toList());
        fileRepository.deleteWithIds(fileIds);
    }
}
