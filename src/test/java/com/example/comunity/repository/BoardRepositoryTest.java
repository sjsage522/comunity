package com.example.comunity.repository;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BoardRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("게시판 조회 테스트")
    void findAllBoardTest() {
        //given
        User user1 = User.createUser(
                "junseok1234",
                "junseok",
                "jun",
                "1234",
                "junseok@example.com"
        );

        User user2 = User.createUser(
                "person55",
                "person",
                "ps55",
                "1234",
                "person@example.com"
        );

        userRepository.join(user1);
        userRepository.join(user2);

        Category category1 = Category.createCategory("game");
        Category category2 = Category.createCategory("economy");

        categoryRepository.create(category1);
        categoryRepository.create(category2);

        Board board1 = Board.createBoard(user1, category1, "game1", "content...");
        Board board2 = Board.createBoard(user1, category1, "game2", "content...");

        Board board3 = Board.createBoard(user1, category2, "economy1", "content...");
        Board board4 = Board.createBoard(user2, category2, "economy2", "user2's content...");

        boardRepository.create(board1);
        boardRepository.create(board2);
        boardRepository.create(board3);
        boardRepository.create(board4);

        em.flush();
        em.clear();

        //when
        List<Board> boards = boardRepository.findAll(); //1

        Board findBoard = boardRepository.findBoardById(board3.getBoardId()); //2

        List<Board> gameBoards = boardRepository.findAllWithCategory("game"); //3

        Board economyBoard = boardRepository.findBoardByIdWithCategory(board4.getBoardId(), "economy"); //4

        List<Board> boardsByUser1 = boardRepository.findBoardByIdWithUser(user1.getUserId()); //5

        List<Board> boardsWithCategory2ByUser2 = boardRepository.findBoardByIdWithCategoryAndUser("economy", user2.getUserId()); //6

        //then
        assertThat(boards.size()).isEqualTo(4); //1

        assertThat(findBoard.getBoardId()).isEqualTo(board3.getBoardId()); //2
        assertThat(findBoard.getCategory().getName()).isEqualTo("economy"); //2 --> LazyLoading
        assertThat(findBoard.getUser().getName()).isEqualTo("junseok"); //2 --> LazyLoading

        assertThat(gameBoards.size()).isEqualTo(2); //3 --> jpql : fetch join

        assertThat(economyBoard.getUser().getName()).isEqualTo("person"); //4 --> fetch join AFTER LazyLoading

        assertThat(boardsByUser1.size()).isEqualTo(3); //5 --> fetch join

        assertThat(boardsWithCategory2ByUser2.size()).isEqualTo(1); //6 --> fetch join
    }

    @Test
    @DisplayName("게시판 삭제 테스트")
    void deleteBoardTest() {
        //given
        User user1 = User.createUser(
                "junseok1234",
                "junseok",
                "jun",
                "1234",
                "junseok@example.com"
        );

        userRepository.join(user1);

        Category category1 = Category.createCategory("game");

        categoryRepository.create(category1);

        Board board1 = Board.createBoard(user1, category1, "game1", "content...");
        Board board2 = Board.createBoard(user1, category1, "game2", "content...");

        boardRepository.create(board1);
        boardRepository.create(board2);

        em.flush();
        em.clear();

        //when
        boardRepository.delete(board2.getBoardId(), user1.getUserId());
        List<Board> boards = boardRepository.findAll();

        //then
        assertThat(boards.size()).isEqualTo(1);
    }

}