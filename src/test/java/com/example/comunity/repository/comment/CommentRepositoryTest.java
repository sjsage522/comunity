package com.example.comunity.repository.comment;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.Comment;
import com.example.comunity.domain.User;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.CommentRepository;
import com.example.comunity.repository.UserRepository;
import com.example.comunity.repository.BoardRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.MethodName.class)
@DisplayName("코멘트 레포지토리 테스트")
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("테스트 01. 답글 작성 테스트")
    void _01_apply() {

        //given
        User user = getUser("tester1234", "tester", "test", "1234", "test@gmail.com");
        Category category = getCategory("coding");
        Board board = getBoard(user, category, "board", "content");

        Comment newComment = Comment.from(
                user,
                board,
                "comment..!"
        );

        userRepository.save(user);
        categoryRepository.save(category);
        boardRepository.save(board);

        //when
        commentRepository.save(newComment);
        em.flush();
        em.clear();

        //then
        assertThat(commentRepository.findById(1L).get().getContent()).isEqualTo("comment..!");
    }

    @Test
    @DisplayName("테스트 02. 답글 삭제 테스트")
    void _02_delete() {
        //given
        User user = getUser("tester1234", "tester", "test", "1234", "test@gmail.com");
        Category category = getCategory("coding");
        Board board = getBoard(user, category, "board", "content");

        Comment newComment = Comment.from(
                user,
                board,
                "comment..!"
        );

        userRepository.save(user);
        categoryRepository.save(category);
        boardRepository.save(board);
        commentRepository.save(newComment);

        em.flush();
        em.clear();

        Comment findComment = commentRepository.findById(1L).get();

        //when
        commentRepository.delete(findComment);

        //then
        assertThat(commentRepository.findById(1L).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("테스트 03. 모든 답글 조회 테스트")
    void _03_findAll() {
    }

    @Test
    @DisplayName("테스트 04. 답글 조회 테스트 (byId)")
    void _04_findCommentById() {
    }

    @Test
    @DisplayName("테스트 05. 모든 답글 삭제 테스트 (byIds)")
    void _05_deleteAllByIds() {
    }

    private Category getCategory(String name) {
        return Category.of(name);
    }

    private Board getBoard(User newUser, Category category, String title, String content) {
        return Board.from(newUser, category, title, content);
    }

    private User getUser(String userId, String name, String nickName, String password, String email) {
        return User.from(userId, name, nickName, password, email);
    }
}