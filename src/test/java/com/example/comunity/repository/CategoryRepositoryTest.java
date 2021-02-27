package com.example.comunity.repository;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CategoryRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("카테고리 이름 변경 테스트")
    void categoryNameModifyTest() {
        //given
        User user = User.createUser(
                "junseok1234",
                "junseok",
                "jun",
                "1234",
                "junseok@example.com");
        userRepository.join(user);

        Category category = Category.createCategory("game");
        categoryRepository.create(category);

        String title = "제목";
        String content = "내용";
        Board board = Board.createBoard(user, category, title, content);
        boardRepository.create(board);

        em.flush();
        em.clear();

        //when
        Board findBoard = boardRepository.findBoardById(board.getBoardId());
        findBoard.getCategory().modifyCategory("economy");

        em.flush();
        em.clear();

        //then
        assertThat(findBoard.getCategory().getName()).isEqualTo("economy"); //변경감지 update
    }

    @Test
    @DisplayName("카테고리 단건 조회 테스트")
    void findCategoryByIdTest() {
        //given
        Category category = Category.createCategory("game");
        categoryRepository.create(category);

        //when
        Category findCategory = categoryRepository.findById(category.getCategoryId());
        Category invalidCategory = categoryRepository.findById(30L);

        //then
        assertThat(findCategory.getName()).isEqualTo("game");
        assertThat(invalidCategory).isNull();
    }
}