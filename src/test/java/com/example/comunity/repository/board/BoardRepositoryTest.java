package com.example.comunity.repository.board;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.User;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.MethodName.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        User getUser = getUser("test", "tester", "tester123", "1234", "test@gmail.com");
        User newUser = userRepository.save(getUser);

        Category getCategory = getCategory("coding");
        Category newCategory = categoryRepository.save(getCategory);

        Board getBoard = getBoard(newUser, newCategory, "title", "content");

        //when
        boardRepository.save(getBoard);

        em.flush(); /* 영속성 컨텍스트의 SQL 쓰기 지연저장소에 있는 쿼리를 날림 */
        em.clear(); /* 1차 캐시 클리어 */

        Board findBoard = boardRepository.findById(1L).get();
        //then
        assertThat(findBoard.getUser().getUserId()).isEqualTo("test");              /* lazy loading */
        assertThat(findBoard.getCategory().getCategoryName()).isEqualTo("coding");  /* lazy loading */
        assertThat(findBoard.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("테스트 02. 게시글 삭제 테스트")
    void _02_delete() {

        //given
        User getUser = getUser("test", "tester", "tester123", "1234", "test@gmail.com");
        User newUser = userRepository.save(getUser);

        Category getCategory = getCategory("coding");
        Category newCategory = categoryRepository.save(getCategory);

        Board getBoard = getBoard(newUser, newCategory, "title", "content");

        boardRepository.save(getBoard);

        em.flush();
        em.clear();

        Board findBoard = boardRepository.findById(1L).get();

        //when
        boardRepository.delete(findBoard);

        //then
        assertThat(boardRepository.findById(1L).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("테스트 03. 모든 게시글 삭제 테스트 (by ids)")
    void _03_deleteAllByIds() {

        //given
        User getUser = getUser("test", "tester", "tester123", "1234", "test@gmail.com");
        User newUser = userRepository.save(getUser);

        Category getCategory = getCategory("coding");
        Category newCategory = categoryRepository.save(getCategory);

        Board getBoard = getBoard(newUser, newCategory, "title", "content");

        boardRepository.save(getBoard);
        boardRepository.save(getBoard);
        boardRepository.save(getBoard);

        em.flush();
        em.clear();

        //when
        System.out.println(boardRepository.findAll().get(0).getBoardId());

        boardRepository.deleteWithIds(Arrays.asList(1L, 2L, 3L));

        //then
        assertThat(boardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("테스트 04. 게시글 조회 테스트 (by id)")
    void _04_findBoardById() {

        //given
        User getUser = getUser("test", "tester", "tester123", "1234", "test@gmail.com");
        User newUser = userRepository.save(getUser);

        Category getCategory = getCategory("coding");
        Category newCategory = categoryRepository.save(getCategory);

        Board getBoard = getBoard(newUser, newCategory, "title", "content");

        boardRepository.save(getBoard);

        em.flush();
        em.clear();

        //when
        Board findBoard = boardRepository.findById(1L).get();

        //then
        assertThat(findBoard.getTitle()).isEqualTo("title");
        assertThat(findBoard.getUser().getUserId()).isEqualTo("test");                  /* lazy loading */
        assertThat(findBoard.getCategory().getCategoryName()).isEqualTo("coding");      /* lazy loading */
    }

    @Test
    @DisplayName("테스트 05. 게시글 조회 테스트 (by id and category)")
    void _05_findBoardByIdWithCategory() {

        //given
        User getUser = getUser("test", "tester", "tester123", "1234", "test@gmail.com");
        User newUser = userRepository.save(getUser);

        Category getCategory = getCategory("coding");
        Category newCategory = categoryRepository.save(getCategory);

        Board getBoard = getBoard(newUser, newCategory, "title", "content");

        boardRepository.save(getBoard);

        em.flush();
        em.clear();

        //when
        Board findBoard = boardRepository.findByBoardIdAndCategoryName(1L, "coding").get();

        //then
        assertThat(findBoard.getTitle()).isEqualTo("title");
        assertThat(findBoard.getUser().getUserId()).isEqualTo("test");                  /* lazy loading */
        assertThat(findBoard.getCategory().getCategoryName()).isEqualTo("coding");      /* lazy loading */
    }


    @Test
    @DisplayName("테스트 06. 모든 게시글 조회 테스트 (by user)")
    void _06_findAllWithUser() {
        //given
        User user = userRepository.save(getUser("user1", "user", "user", "1234", "user@test.com"));
        Category newCategory = categoryRepository.save(getCategory("coding"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));

        em.flush();
        em.clear();

        //when
//        List<Board> boards = boardRepository.findAllWithUser(user.getUserId());
        List<Board> boards = boardRepository.findAll();

        //then
        assertThat(boards.size()).isEqualTo(3);
    }

    /* TODO method naming 수정 필요 */
    @Test
    @DisplayName("테스트 07. 모든 게시글 조회 테스트 (by category)")
    void _07_findAllWithCategory() {
        //TODO 게시글 페이징 페스트

        //given
        User user = userRepository.save(getUser("user1", "user", "user", "1234", "user@test.com"));
        Category newCategory = categoryRepository.save(getCategory("coding"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));

        em.flush();
        em.clear();

        //when
        List<Board> zeroPage = boardRepository.findAllWithCategory("coding", PageRequest.of(0, 2)) /* 0페이지 -> 2개 */
                .stream()
                .collect(Collectors.toList());

        List<Board> onePage = boardRepository.findAllWithCategory("coding", PageRequest.of(1, 2))
                .stream()
                .collect(Collectors.toList());

        //then
        assertThat(boardRepository.findAll().size()).isEqualTo(3); /* 전체 게시글 수 -> 3개 */
        assertThat(zeroPage.size()).isEqualTo(2);                  /* 0페이지 -> 2개 */
        assertThat(onePage.size()).isEqualTo(1);                   /* 1페이지 -> 1개 */
    }

    @Test
    @DisplayName("테스트 08. 모든 게시글 조회 테스트")
    void _08_findAllByOrderByBoardIdDesc() {
        //TODO 게시글 페이징 테스트

        //given
        User user = userRepository.save(getUser("user1", "user", "user", "1234", "user@test.com"));
        Category newCategory = categoryRepository.save(getCategory("coding"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));
        boardRepository.save(getBoard(user, newCategory, "user_title", "user_content"));

        //when
        List<Board> zeroPage = boardRepository.findAllByOrderByBoardIdDesc(PageRequest.of(0, 3))
                .stream()
                .collect(Collectors.toList());

        List<Board> onePage = boardRepository.findAllByOrderByBoardIdDesc(PageRequest.of(1, 3))
                .stream()
                .collect(Collectors.toList());

        //then
        assertThat(boardRepository.findAll().size()).isEqualTo(4);
        assertThat(zeroPage.size()).isEqualTo(3);
        assertThat(onePage.size()).isEqualTo(1);
    }
    /*                            */

    private Category getCategory(String name) {
        return Category.of(name);
    }

    private Board getBoard(User newUser, Category category, String title, String content) {
        return Board.from(newUser, category, title, content);
    }

    private User getUser(String userId, String name, String nickName, String password, String email) {
        return User.of(userId, name, nickName, password, email);
    }
}