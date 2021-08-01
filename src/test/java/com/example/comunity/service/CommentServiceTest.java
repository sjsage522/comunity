package com.example.comunity.service;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.Comment;
import com.example.comunity.domain.User;
import com.example.comunity.dto.comment.CommentApplyRequest;
import com.example.comunity.dto.comment.CommentUpdateRequest;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@DisplayName("서비스 테스트 (comment)")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BoardRepository boardRepository;

    @Test
    @DisplayName("[성공 테스트] 답글 달기")
    void apply_succeed_test1() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category category = Category.from("game");

        Board board = Board.of(user, category, "testerTitle", "content");
        ReflectionTestUtils.setField(board, "id", 1L);

        Comment comment = Comment.of(user, board, "content");
        CommentApplyRequest commentApplyRequest = CommentApplyRequest.from("content");

        //when
        when(boardRepository.findById(board.getId()))
                .thenReturn(java.util.Optional.of(board));
        when(commentRepository.save(any()))
                .thenReturn(comment);
        Comment newComment = commentService.apply(user, board.getId(), commentApplyRequest);

        //then
        assertThat(newComment.getContent()).isEqualTo("content");
    }

    @Test
    @DisplayName("[성공 테스트] 답글의 답글 달기")
    void apply_succeed_test2() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category category = Category.from("game");

        Board board = Board.of(user, category, "testerTitle", "content");
        ReflectionTestUtils.setField(board, "id", 1L);

        Comment comment1 = Comment.of(user, board, "content1");
        ReflectionTestUtils.setField(comment1, "id", 1L);

        Comment comment2 = Comment.of(user, board, "content2");
        CommentApplyRequest commentApplyRequest = CommentApplyRequest.of(comment1.getId(), comment2.getContent());

        //when
        when(boardRepository.findById(1L))
                .thenReturn(java.util.Optional.of(board));
        when(commentRepository.findById(1L))
                .thenReturn(java.util.Optional.of(comment1)); //parent comment
        when(commentRepository.save(any()))
                .thenReturn(comment2);

        commentService.apply(user, board.getId(), commentApplyRequest);

        //then
        assertThat(comment1.getChildren().size()).isEqualTo(1);
        assertThat(comment1.getChildren().get(0).getContent()).isEqualTo("content2");
    }

    @Test
    @DisplayName("[성공 테스트] 답글 삭제")
    void delete_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category category = Category.from("game");
        Board board = Board.of(user, category, "testerTitle", "content");

        Comment comment = Comment.of(user, board, "content");
        ReflectionTestUtils.setField(comment, "id", 1L);

        //when
        when(commentRepository.findById(comment.getId()))
                .thenReturn(java.util.Optional.of(comment));

        commentService.delete(user, comment.getId());

        //then
        then(commentRepository).should(times(1)).delete(comment);
    }

    @Test
    @DisplayName("[실패 테스트] 답글 삭제 - 다른 사용자의 답글은 삭제할 수 없음")
    void delete_failed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category category = Category.from("game");
        Board board = Board.of(user, category, "testerTitle", "content");

        Comment comment = Comment.of(user, board, "content");
        ReflectionTestUtils.setField(comment, "id", 1L);

        User otherUser = User.of("otherId", "person", "person", "1234", "person@gmail.com");

        //when
        when(commentRepository.findById(comment.getId()))
                .thenReturn(java.util.Optional.of(comment));

        //then
        assertThrows(NoMatchUserInfoException.class,
                () -> commentService.delete(otherUser, comment.getId())
        );
    }

    @Test
    @DisplayName("[성공 테스트] 답글 수정")
    void update_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category category = Category.from("game");
        Board board = Board.of(user, category, "testerTitle", "content");

        Comment comment = Comment.of(user, board, "content");
        ReflectionTestUtils.setField(comment, "id", 1L);

        CommentUpdateRequest request = CommentUpdateRequest.from("UPDATE_CONTENT");

        //when
        when(commentRepository.findById(1L))
                .thenReturn(java.util.Optional.of(comment));

        commentService.update(user, comment.getId(), request);

        //then
        assertThat(comment.getContent()).isEqualTo("UPDATE_CONTENT");
    }

    @Test
    @DisplayName("[실패 테스트] 답글 수정 - 다른 사람의 답글은 수정할 수 없음")
    void update_failed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category category = Category.from("game");
        Board board = Board.of(user, category, "testerTitle", "content");

        Comment comment = Comment.of(user, board, "content");
        ReflectionTestUtils.setField(comment, "id", 1L);

        User otherUser = User.of("otherId", "person", "person", "1234", "person@gmail.com");

        CommentUpdateRequest request = CommentUpdateRequest.from("UPDATE_CONTENT");

        //when
        when(commentRepository.findById(1L))
                .thenReturn(java.util.Optional.of(comment));

        //then
        assertThrows(NoMatchUserInfoException.class,
                () -> commentService.update(otherUser, comment.getId(), request)
        );
    }

    @Test
    @DisplayName("[성공 테스트] 답글 페이지 조회")
    void findAll_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category category = Category.from("game");

        Board board = Board.of(user, category, "testerTitle", "content");
        ReflectionTestUtils.setField(board, "id", 1L);

        List<Comment> commentList = IntStream
                .rangeClosed(1, 20
                ).mapToObj(i -> Comment.of(user, board, "content" + i))
                .collect(Collectors.toList());

        List<Comment> onePageList = IntStream
                .rangeClosed(0, 9)
                .mapToObj(commentList::get)
                .collect(Collectors.toList());

        int page = 0;

        //when
        when(boardRepository.findById(board.getId()))
                .thenReturn(java.util.Optional.of(board));
        when(commentRepository.findAllByBoardIdIfParentIsNullWithPaging(board.getId(), PageRequest.of(page, 10)))
                .thenReturn(new PageImpl<>(onePageList, PageRequest.of(page, 10), 10));

        List<Comment> result = commentService.findAll(board.getId(), page);

        //then
        assertThat(result.size()).isEqualTo(10);
    }
}
