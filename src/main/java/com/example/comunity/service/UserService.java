package com.example.comunity.service;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Comment;
import com.example.comunity.domain.UploadFile;
import com.example.comunity.domain.User;
import com.example.comunity.dto.user.UserDeleteRequest;
import com.example.comunity.dto.user.UserJoinRequest;
import com.example.comunity.dto.user.UserUpdateRequest;
import com.example.comunity.exception.DuplicateUserIdException;
import com.example.comunity.exception.DuplicateUserNickNameException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.FileRepository;
import com.example.comunity.repository.UserRepository;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public User join(final UserJoinRequest userJoinRequest) {

        userRepository.findByUserIdOrNickName(userJoinRequest.getUserId(), userJoinRequest.getNickName())
                .ifPresent(user -> {
                    if (user.getUserId().equals(userJoinRequest.getUserId())) throw new DuplicateUserIdException("이미 존재하는 아이디 입니다.");
                    else throw new DuplicateUserNickNameException("이미 존재하는 별명 입니다.");
                });

        User newUser = User.from(userJoinRequest);
        return userRepository.save(newUser);
    }

    @Transactional
    public User update(final String id, final UserUpdateRequest userUpdateRequest, User loginUser) {
        /* 영속상태의 entity */
        User findUser = findById(id);

        if (!loginUser.getUserId().equals(findUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 정보를 수정할 수 없습니다.");

        /* dirty check */
        findUser.changeName(userUpdateRequest.getName());
        findUser.changeNickname(userUpdateRequest.getNickName());
        findUser.changePassword(userUpdateRequest.getPassword());
        /* 트랜잭션 커밋시점에 1차캐시의 스냅샷과 영속 상태의 entity 정보와 비교 */
        /* 변경된 부분을 update 쿼리를 통해(영속성 컨텍스트 SQL 저장소) db 데이터를 수정 */

        return findUser;
    }

    @Transactional
    public int delete(final String userId, final UserDeleteRequest userDeleteRequest, final User loginUser) {
        User findUser = findById(userId);

        if (!loginUser.getUserId().equals(findUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 정보를 삭제할 수 없습니다.");

        List<Board> boards = boardRepository.findAllByUserId(findUser.getId());
        List<Long> boardIds = boards.stream()
                .map(Board::getBoardId)
                .collect(Collectors.toList());

        for (Long boardId : boardIds) {
            deleteRelatedToBoard(boardId, fileRepository, commentRepository);
        }

        boardRepository.deleteWithIds(boardIds);

        if (findUser.getUserId().equals(userDeleteRequest.getUserId()) &&
                findUser.getPassword().equals(userDeleteRequest.getPassword())) {
            return userRepository.deleteByUserId(userId);
        }
        return 0;
    }

    public User findById(final String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoMatchUserInfoException("존재하지 않는 사용자 입니다."));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    static void deleteRelatedToBoard(Long boardId, FileRepository fileRepository, CommentRepository commentRepository) {
        List<UploadFile> uploadFiles = fileRepository.findAll(boardId);
        List<Long> fileIds = uploadFiles.stream()
                .map(UploadFile::getUploadFileId)
                .collect(Collectors.toList());
        fileRepository.deleteAllByIds(fileIds);

        List<Comment> comments = commentRepository.findAll(boardId);
        List<Long> commentIds = comments.stream()
                .map(Comment::getCommentId)
                .collect(Collectors.toList());
        commentRepository.deleteAllByIds(commentIds);
    }
}
