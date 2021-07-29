package com.example.comunity.repository;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.CategoryName;
import com.example.comunity.domain.User;
import com.example.comunity.exception.NoMatchBoardInfoException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("레포지토리 테스트 (board)")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-h2")
@TestMethodOrder(MethodOrderer.MethodName.class)
@DirtiesContext
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @BeforeEach
    void before(@Autowired EntityManager em) {
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Category gameCategory = Category.from("game");
        em.persist(user);
        em.persist(codingCategory);
        em.persist(gameCategory);

        em.persist(Board.of(user, codingCategory, "title1", "content1"));
        em.persist(Board.of(user, codingCategory, "title2", "content2"));
        em.persist(Board.of(user, codingCategory, "title3", "content3"));
        em.persist(Board.of(user, gameCategory, "title4", "content4"));
        em.persist(Board.of(user, gameCategory, "title5", "content5"));
        em.persist(Board.of(user, gameCategory, "title6", "content6"));
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 아이디와 카테고리 이름으로 게시글 단건 조회")
    void _01_findByBoardIdAndCategoryName_succeed_test() {
        Board findBoard = boardRepository.findByBoardIdAndCategoryName(6L, CategoryName.GAME)
                .orElseThrow(NoMatchBoardInfoException::new);


        assertThat(findBoard.getTitle()).isEqualTo("title6");
        assertThat(findBoard.getContent()).isEqualTo("content6");
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 아이디 리스트로 게시글들 삭제")
    void _02_deleteWithIds_succeed_test() {
        System.out.println(boardRepository.findAll());
        boardRepository.deleteWithIds(List.of(7L, 8L, 9L));

        List<Board> all = boardRepository.findAll();
        assertThat(all.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("[성공 테스트] 카테고리 이름으로 전체 게시글 페이징 조회")
    void _03findAllByCategoryNameWithPaging_succeed_test() {
        List<Board> boardList = boardRepository.findAllByCategoryNameWithPaging(CategoryName.CODING, PageRequest.of(0, 2))
                .stream()
                .collect(Collectors.toList());

        assertThat(boardList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("[성공 테스트] 전체 게시글 페이징 조회")
    void _04_findAllWithPaging_succeed_test() {
        List<Board> onePageList = boardRepository.findAllWithPaging(PageRequest.of(0, 5))
                .stream()
                .collect(Collectors.toList());

        List<Board> twoPageList = boardRepository.findAllWithPaging(PageRequest.of(1, 5))
                .stream()
                .collect(Collectors.toList());

        assertThat(onePageList.size()).isEqualTo(5);
        assertThat(twoPageList.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("[성공 테스트] 사용자 아이디로 모든 게시글 조회")
    void _05_findAllByUser_UserId_succeed_test() {
        List<Board> boardList = boardRepository.findAllByUser_UserId("testId");

        assertThat(boardList.size()).isEqualTo(6);
    }
}