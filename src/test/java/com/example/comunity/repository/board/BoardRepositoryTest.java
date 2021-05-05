package com.example.comunity.repository.board;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.User;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.MethodName.class)
@DisplayName("게시판 레포지토리 Custom")
class BoardRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("테스트 01. 게시글 업로드 테스트")
    void _01_upload() {

        //given
        User getUesr = getUser("test", "tester", "tester123", "1234", "test@gmail.com");
        User newUser = userRepository.join(getUesr);

        Category getCategory = getCategory("coding");
        Category newCategory = categoryRepository.create(getCategory);

        Board getBoard = getBoard(newUser, newCategory, "title", "content");

        //when
        boardRepository.upload(getBoard);

        em.flush(); /* 영속성 컨텍스트의 SQL 쓰기 지연저장소에 있는 쿼리를 날림 */
        em.clear(); /* 1차 캐시 클리어 */

        Board findBoard = boardRepository.findBoardById(1L);
        //then
        assertThat(findBoard.getUser().getUserId()).isEqualTo("test");              /* lazy loading */
        assertThat(findBoard.getCategory().getCategoryName()).isEqualTo("coding");  /* lazy loading */
        assertThat(findBoard.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("테스트 02. 게시글 삭제 테스트")
    void _02_delete() {
    }

    @Test
    @DisplayName("테스트 03. 모든 게시글 삭제 테스트 (by ids)")
    void _03_deleteAllByIds() {
    }

    @Test
    @DisplayName("테스트 04. 게시글 조회 테스트 (by id)")
    void _04_findBoardById() {
    }

    @Test
    @DisplayName("테스트 05. 게시글 조회 테스트 (by id and category)")
    void _05_findBoardByIdWithCategory() {
    }


    @Test
    @DisplayName("테스트 06. 모든 게시글 조회 테스트 (by user)")
    void _06_findAllWithUser() {
        //given
        User user = userRepository.join(getUser("user1", "user", "user", "1234", "user@test.com"));
        Category newCategory = categoryRepository.create(getCategory("coding"));
        boardRepository.upload(getBoard(user, newCategory, "user_title", "user_content"));

        //when
        List<Board> boards = boardRepository.findAllWithUser(user.getUserId());

        //then
        assertThat(boards.size()).isEqualTo(1);
    }

    /* TODO method naming 수정 필요 */
    @Test
    @DisplayName("테스트 07. 모든 게시글 조회 테스트 (by category)")
    void _07_findAllWithCategory() {
        //TODO 게시글 페이징 페스트
    }

    @Test
    @DisplayName("테스트 08. 모든 게시글 조회 테스트")
    void _08_findAllByOrderByBoardIdDesc() {
        //TODO 게시글 페이징 테스트
    }
    /*                            */

    private Category getCategory(String name) {
        return Category.createCategory(name);
    }

    private Board getBoard(User newUser, Category category, String title, String content) {
        return Board.createBoard(newUser, category, title, content);
    }

    private User getUser(String userId, String name, String nickName, String password, String email) {
        return User.createUser(userId, name, nickName, password, email);
    }
}