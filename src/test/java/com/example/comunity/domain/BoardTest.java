package com.example.comunity.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {

    @Test
    @DisplayName("게시판 생성 테스트")
    void createBoardTest() {
        //given
        User user = User.createUser(
                "junseok1234",
                "junseok",
                "jun",
                "1234",
                "junseok@example.com",
                "010-0000-0000");

        Category category = Category.createCategory("game");

        String title = "제목";
        String content = "내용";
        Board board = Board.createBoard(user, category, title, content);

        //when
        Category getCategory = board.getCategory();
        String categoryName = getCategory.getName();

        //then
        assertThat(categoryName).isEqualTo("game");
    }


    @Test
    @DisplayName("게시판과 카테고리 연관관계 메소드 테스트")
    void boardAndCategoryRelationTest() {
        //given
        User user = User.createUser(
                "junseok1234",
                "junseok",
                "jun",
                "1234",
                "junseok@example.com",
                "010-0000-0000");

        Category category = Category.createCategory("game");

        String title = "제목";
        String content = "내용";
        Board board = Board.createBoard(user, category, title, content);



        //when
        Category newCategory = Category.createCategory("economy");
        board.changeCategory(newCategory);

        Category getCategory = board.getCategory();
        String categoryName = getCategory.getName();

        //then
        assertThat(categoryName).isEqualTo("economy");
        assertThat(board.getTitle()).isEqualTo("제목");
        assertThat(board.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("게시판과 카테고리 연관관계 메소드 테스트2")
    void boardAndCategoryRelationTest2() {
        //given
        User user = User.createUser(
                "junseok1234",
                "junseok",
                "jun",
                "1234",
                "junseok@example.com",
                "010-0000-0000");

        Category category = Category.createCategory("game");

        String title = "제목";
        String content = "내용";
        Board board = Board.createBoard(user, category, title, content);

        //when
        category.addBoard(board);
        category.addBoard(board);

        //then
        assertThat(category.getBoards().size()).isEqualTo(1);
    }
}