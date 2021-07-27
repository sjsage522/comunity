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
import com.example.comunity.repository.CommentRepository;
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

    /**
     * 사용자 아이디와 별명은 unique 하기 때문에 먼저 검사 후 등록되어 있지 않은 사용자라면,
     * User entity 를 생성하고 save
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

    /**
     * session 으로 부터 얻어온 사용자와 삭제될 사용자가 일치할 경우에만 삭제 진행
     */
    @Transactional
    public int delete(
            final String userId,
            final UserDeleteRequest userDeleteRequest,
            final User loginUser) {
        User findUser = findById(userId);

        if (!loginUser.getUserId().equals(findUser.getUserId()))
            throw new NoMatchUserInfoException("다른 사용자의 정보를 삭제할 수 없습니다.");

        List<Board> boards = boardRepository.findAllByUserId(loginUser.getId());
        List<Long> boardIds = boards.stream()
                .map(Board::getBoardId)
                .collect(Collectors.toList());

        for (Long boardId : boardIds) deleteRelatedToBoard(boardId, fileRepository, commentRepository);

        // 사용자를 삭제하기 전에 사용자 외래키를 갖는 게시판을 삭제한다.
        boardRepository.deleteWithIds(boardIds);

        // 아이디와 패스워드가 일치한다면 삭제 진행
        // TODO : 인코딩된 패스워드 비교처리 (현재 정상작동 안됨)
        if (findUser.getUserId().equals(userDeleteRequest.getUserId()) &&
                findUser.getPassword().equals(userDeleteRequest.getPassword())) {
            return userRepository.deleteByUserId(userId);
        }
        return 0;
    }

    public User findById(
            final String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(NoMatchUserInfoException::new);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 특정 게시판에 첨부되어 있는 파일과 답글들을 삭제하는 메소드
    // 참조무결성 제약조건 때문에 먼저 사용자와 게시판 외래키를 갖고있는 파일과 답글들을 먼저 삭제해야 한다.
    // TODO : 메소드 분리할 것
    static void deleteRelatedToBoard(Long boardId, FileRepository fileRepository, CommentRepository commentRepository) {
        List<UploadFile> uploadFiles = fileRepository.findAllByBoard_BoardId(boardId);
        List<Long> fileIds = uploadFiles.stream()
                .map(UploadFile::getUploadFileId)
                .collect(Collectors.toList());
        fileRepository.deleteWithIds(fileIds);

        List<Comment> comments = commentRepository.findAllByBoard_BoardId(boardId);
        List<Long> commentIds = comments.stream()
                .map(Comment::getCommentId)
                .collect(Collectors.toList());
        commentRepository.deleteWithIds(commentIds);
    }
}
