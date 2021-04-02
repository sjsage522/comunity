package com.example.comunity.service;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Comment;
import com.example.comunity.domain.UploadFile;
import com.example.comunity.domain.User;
import com.example.comunity.dto.user.UserDeleteDto;
import com.example.comunity.dto.user.UserJoinDto;
import com.example.comunity.dto.user.UserUpdateDto;
import com.example.comunity.exception.DuplicateUserIdException;
import com.example.comunity.exception.DuplicateUserNickNameException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.FileRepository;
import com.example.comunity.repository.UserRepository;
import com.example.comunity.repository.board.BoardRepository;
import com.example.comunity.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
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
    public User join(final UserJoinDto userJoinDto) {
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
    public User update(final String id, final UserUpdateDto userUpdateDto, User loginUser) {
        /* 영속상태의 entity */
        User findUser = findById(id);

        if (!loginUser.getUserId().equals(findUser.getUserId())) throw new NoMatchUserInfoException("다른 사용자의 정보를 수정할 수 없습니다.");

        /* dirty check */
        findUser.changeName(userUpdateDto.getName());
        findUser.changeNickname(userUpdateDto.getNickName());
        findUser.changePassword(userUpdateDto.getPassword());
        /* 트랜잭션 커밋시점에 1차캐시의 스냅샷과 영속 상태의 entity 정보와 비교 */
        /* 변경된 부분을 update 쿼리를 통해(영속성 컨텍스트 SQL 저장소) db 데이터를 수정 */

        userUpdateDto.setUserId(id);

        return findUser;
    }

    @Transactional
    public int delete(final String id, final UserDeleteDto userDeleteDto, final User loginUser) {
        User findUser = findById(id);

        if (!loginUser.getUserId().equals(findUser.getUserId())) throw new NoMatchUserInfoException("다른 사용자의 정보를 삭제할 수 없습니다.");

        List<Board> boards = boardRepository.findAllWithUser(id);
        List<Long> boardIds = boards.stream()
                .map(Board::getBoardId)
                .collect(Collectors.toList());

        for (Long boardId : boardIds) {
            deleteRelatedToBoard(boardId, fileRepository, commentRepository);
        }

        boardRepository.deleteAllByIds(boardIds);

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
